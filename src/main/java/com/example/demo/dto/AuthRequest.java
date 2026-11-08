
package com.example.demo.dto;

public record AuthRequest(
        String username,
        String email,
        String password
) {}
