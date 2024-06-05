package dev.mmaksymko.users.mapper;

import dev.mmaksymko.users.dto.UserRequest;
import dev.mmaksymko.users.dto.UserResponse;
import dev.mmaksymko.users.models.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    public User toEntity(UserRequest request) {
        return User
                .builder()
                .id(request.id())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .role(request.role())
                .registeredAt(request.registeredAt())
                .pfpUrl(request.pfpUrl())
                .build();
    }

    public User toEntity(UserResponse response) {
        return User
                .builder()
                .id(response.getId())
                .firstName(response.getFirstName())
                .lastName(response.getLastName())
                .email(response.getEmail())
                .role(response.getRole())
                .registeredAt(response.getRegisteredAt())
                .pfpUrl(response.getPfpUrl())
                .build();
    }

    public UserResponse toResponse(User entity) {
        return UserResponse
                .builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .role(entity.getRole())
                .registeredAt(entity.getRegisteredAt())
                .pfpUrl(entity.getPfpUrl())
                .build();
    }
}
