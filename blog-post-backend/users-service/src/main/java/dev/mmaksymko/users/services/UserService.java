package dev.mmaksymko.users.services;

import dev.mmaksymko.users.clients.ImageClient;
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
//import org.springframework.security.core.context.SecurityContextHolder;
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
//    private final AuthService authService;
    private final UserMapper userMapper;
    private final UserProducer userProducer;
    private final ImageClient imageClient;
    private static final String PPF_BUCKET = "pfp";

//    public User getCurrentUser() {
//        String email = getCurrentUsersEmail();
//        return getUserByEmail(email).map(userMapper::toEntity).orElseThrow();
//    }

//    public String getCurrentUsersEmail() {
//        return SecurityContextHolder.getContext().getAuthentication().getName();
//    }

//    public UserResponse getCurrentUserResponse(){
//        return userMapper.toResponse(getCurrentUser());
//    }

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
        User user = userRepository.findById(userId).orElseThrow();

//        authService.checkEditAuthority(user);

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
        User user = userRepository.findById(userId).orElseThrow();

        String imageUrl = imageClient.uploadImage(image, PPF_BUCKET);
        user.setPfpUrl(imageUrl);

        UserResponse response = userMapper.toResponse(userRepository.save(user));
        userProducer.sendUpdatedEvent(response);

        return response;
    }

    @Transactional
    @Modifying
    public void deleteUserById(Long userId){
        User user = userRepository.findById(userId).orElseThrow();

//        authService.checkEditAuthority(user);

        userRepository.deleteById(userId);

        UserResponse response = userMapper.toResponse(userRepository.save(user));
        userProducer.sendDeletedEvent(response);
    }
}
