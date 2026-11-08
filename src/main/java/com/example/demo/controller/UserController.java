
package com.example.demo.controller;

import com.example.demo.dto.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Регистрация пользователя
    @PostMapping("/register")
    public UserResponse register(@RequestBody User user) {
        User savedUser = userService.register(
                user.getUsername(),
                user.getEmail(),
                user.getPassword()
        );

        return UserResponse.from(savedUser);
    }

    // Получение всех пользователей
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    // Получение пользователя по ID
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return UserResponse.from(userService.getUserById(id));
    }
}
