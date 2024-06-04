package dev.mmaksymko.gateway.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.mmaksymko.gateway.dto.UserRequest;
import dev.mmaksymko.gateway.dto.UserResponse;
import dev.mmaksymko.gateway.mapper.UserMapper;
import dev.mmaksymko.gateway.models.User;
import dev.mmaksymko.gateway.models.UserRole;
import dev.mmaksymko.gateway.repositories.UserRepository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
//    private final AuthService authService;
    private final UserMapper userMapper;

    public User getCurrentUser() {
        String email = getCurrentUsersEmail();
        return getUserByEmail(email).map(userMapper::toEntity).orElseThrow();
    }

    public String getCurrentUsersEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public UserResponse getCurrentUserResponse(){
        return userMapper.toResponse(getCurrentUser());
    }

    public Optional<UserResponse> getUserById(Long userId){
        return userRepository.findById(userId).map(userMapper::toResponse);
    }

    public Optional<UserResponse> getUserByEmail(String email){
        return userRepository.findByEmail(email).map(userMapper::toResponse);
    }

    public Slice<UserRole> getRoles(){
        return new SliceImpl<>(List.of(UserRole.values()));
    }

    @Transactional
    @Modifying
    public Mono<User> saveUser(User user){
        return Mono.fromCallable(() -> userRepository.save(user));
    }

    @Transactional
    @Modifying
    public UserResponse updateUser(UserRequest userRequest, Long userId){
        User user = userRepository.findById(userId).orElseThrow();

//        authService.checkEditAuthority(user);

        user.setEmail(userRequest.email());
        user.setFirstName(userRequest.firstName());
        user.setLastName(userRequest.lastName());

        return userMapper.toResponse(userRepository.save(user));
    }


    @Transactional
    @Modifying
    public void deleteById(Long userId){
        User user = userRepository.findById(userId).orElseThrow();

//        authService.checkEditAuthority(user);

        userRepository.deleteById(userId);
    }

}
