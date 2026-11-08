
package com.example.demo.controller;

import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Получение списка всех заказов
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // Получение заказа по ID
    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    // Получение заказов покупателя
    @GetMapping("/buyer/{buyerId}")
    public List<Order> getOrdersByBuyer(@PathVariable Long buyerId) {
        return orderService.getOrdersByBuyer(buyerId);
    }

    // Получение заказов продавца
    @GetMapping("/seller/{sellerId}")
    public List<Order> getOrdersBySeller(@PathVariable Long sellerId) {
        return orderService.getOrdersBySeller(sellerId);
    }

    // Создание заказа
    @PostMapping
    public Order createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(
                request.buyerId(),
                request.itemId()
        );
    }

    // Данные для создания заказа
    public record CreateOrderRequest(
            Long buyerId,
            Long itemId
    ) {}
}
