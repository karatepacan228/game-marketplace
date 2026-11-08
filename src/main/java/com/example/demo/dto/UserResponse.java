package com.example.demo.dto;

import com.example.demo.entity.User;
import com.example.demo.entity.Role;
import java.math.BigDecimal;

public record UserResponse(
        Long id,
        String username,
        String email,
        Role role,
        BigDecimal balance
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getBalance()
        );
    }
}