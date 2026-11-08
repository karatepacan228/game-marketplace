
package com.example.demo.controller;

import com.example.demo.dto.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    // Пополнение баланса покупателя
    @PostMapping("/deposit")
    public UserResponse depositBalance(
            @RequestBody DepositRequest request) {

        User buyer = userService.depositBalance(
                request.adminId(),
                request.buyerId(),
                request.amount()
        );

        return UserResponse.from(buyer);
    }

    // Данные для пополнения баланса
    public record DepositRequest(
            Long adminId,
            Long buyerId,
            BigDecimal amount
    ) {}
}
