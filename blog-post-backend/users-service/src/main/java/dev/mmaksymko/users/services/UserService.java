package dev.mmaksymko.users.services;

import dev.mmaksymko.users.clients.ImageClient;
import dev.mmaksymko.users.configs.exceptions.ForbiddenException;
import dev.mmaksymko.users.configs.security.Claims;
import dev.mmaksymko.users.dto.UserRequest;
import dev.mmaksymko.users.dto.UserUpdateRequest;
import dev.mmaksymko.users.dto.UserResponse;
import dev.mmaksymko.users.mapper.UserMapper;
import dev.mmaksymko.users.models.User;
import dev.mmaksymko.users.models.UserRole;
import dev.mmaksymko.users.repositories.UserRepository;
import dev.mmaksymko.users.services.kafka.UserProducer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserProducer userProducer;
    private final ImageClient imageClient;
    private final Claims claims;

    private static final String PPF_BUCKET = "pfp";

    public UserResponse getUserById(Long userId){
        return userRepository.findById(userId).map(userMapper::toResponse).orElseThrow();
    }

    public UserResponse getUserByEmail(String email){
        return userRepository.findByEmail(email).map(userMapper::toResponse).orElseThrow();
    }

    public Page<UserRole> getRoles(){
        return new PageImpl<>(List.of(UserRole.values()), Pageable.unpaged(), UserRole.values().length);
    }

    @Transactional
    @Modifying
    public UserResponse saveUser(UserRequest request){
        User user = userMapper.toEntity(request);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Transactional
    @Modifying
    public UserResponse updateUser(Long userId, UserUpdateRequest userRequest){
        if (!isUserAllowedToModify(userId)) {
            throw new ForbiddenException("You don't have permission to modify this user");
        }

        User user = userRepository.findById(userId).orElseThrow();

        user.setEmail(userRequest.email());
        user.setFirstName(userRequest.firstName());
        user.setLastName(userRequest.lastName());

        UserResponse response = userMapper.toResponse(userRepository.save(user));
        userProducer.sendUpdatedEvent(response);

        return response;
    }

    @Transactional
    @Modifying
    @CircuitBreaker(name = "circuit-breaker-pfp")
    public UserResponse updateUserPfp(Long userId, MultipartFile image) {
        if (!isUserAllowedToModify(userId)) {
            throw new ForbiddenException("You don't have permission to modify this user");
        }

        User user = userRepository.findById(userId).orElseThrow();

        String imageUrl = imageClient.uploadImage(PPF_BUCKET, image);
        user.setPfpUrl(imageUrl);

        UserResponse response = userMapper.toResponse(userRepository.save(user));
        userProducer.sendUpdatedEvent(response);

        return response;
    }

    @Transactional
    @Modifying
    public void deleteUserById(Long userId){
        if (!isUserAllowedToModify(userId)) {
            throw new ForbiddenException("You don't have permission to modify this user");
        }

        User user = userRepository.findById(userId).orElseThrow();

        userRepository.deleteById(userId);

        UserResponse response = userMapper.toResponse(userRepository.save(user));
        userProducer.sendDeletedEvent(response);
    }

    @Transactional
    @Modifying
    public UserResponse appointAdmin(Long userId){
        return changeRoleById(userId, UserRole.ADMIN);
    }

    @Transactional
    @Modifying
    public UserResponse fireAdmin(Long userId){
        return changeRoleById(userId, UserRole.USER);
    }

    private UserResponse changeRoleById(Long userId, UserRole role){
        User user = userRepository.findById(userId).orElseThrow();

        if (user.getRole()!=UserRole.SUPER_ADMIN) {
            user.setRole(role);
        }

        UserResponse response = userMapper.toResponse(userRepository.save(user));
        userProducer.sendUpdatedEvent(response);

        return response;
    }

    private boolean isUserAllowedToModify(Long userId) {
        return Optional.ofNullable(claims.getClaim("role"))
                .map(Object::toString)
                .map(role -> role.equals("ADMIN") || role.equals("SUPER_ADMIN") || userId.equals(getUserId()))
                .orElse(false);
    }

    private Long getUserId() {
        return Optional.ofNullable(claims.getClaim("id"))
                .map(Object::toString)
                .map(Long::parseLong)
                .orElse(null);
    }
}
