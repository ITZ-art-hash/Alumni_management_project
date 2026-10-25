package com.technoboost.dto.response;

import com.technoboost.entity.User;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String role,
        boolean emailVerified,
        boolean active,
        LocalDateTime createdDate
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().name(),
                user.isEmailVerified(),
                user.isActive(),
                user.getCreatedDate());
    }
}
