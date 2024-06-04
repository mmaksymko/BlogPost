package dev.mmaksymko.gateway.mapper;

import dev.mmaksymko.gateway.dto.UserResponse;
import dev.mmaksymko.gateway.models.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
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
