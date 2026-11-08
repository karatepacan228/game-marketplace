
package com.example.demo.controller;

import com.example.demo.dto.UserResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final OrderService orderService;

    // Пополнение баланса пользователя
    @PostMapping("/deposit")
    public UserResponse depositBalance(
            @RequestBody DepositRequest request,
            Authentication authentication) {

        User admin = userService.getUserByUsername(
                authentication.getName()
        );

        User buyer = userService.depositBalance(
                admin.getId(),
                request.userId(),
                request.amount()
        );

        return UserResponse.from(buyer);
    }

    // Отмена заказа администратором
    @PostMapping("/orders/{orderId}/cancel")
    public Order cancelOrder(
            @PathVariable Long orderId,
            Authentication authentication) {

        User admin = userService.getUserByUsername(
                authentication.getName()
        );

        return orderService.cancelOrderByAdmin(
                orderId,
                admin.getId()
        );
    }

    // Данные для пополнения баланса
    public record DepositRequest(
            Long userId,
            BigDecimal amount
    ) {}
}
