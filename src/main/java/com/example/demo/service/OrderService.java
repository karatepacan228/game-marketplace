package com.example.demo.service;

import com.example.demo.entity.Item;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.User;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    // Создание заказа
    @Transactional
    public Order createOrder(Long buyerId, Long itemId) {

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Покупатель не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Товар не найден"));

        if (!item.isAvailable()) {
            throw new IllegalStateException("Товар уже недоступен");
        }

        if (item.getSeller().getId().equals(buyerId)) {
            throw new IllegalStateException(
                    "Нельзя покупать собственный товар");
        }

        Order order = new Order();

        order.setBuyer(buyer);
        order.setSeller(item.getSeller());
        order.setItem(item);
        order.setPrice(item.getPrice());
        order.setStatus(OrderStatus.NEW);

        item.setAvailable(false);

        itemRepository.save(item);

        return orderRepository.save(order);
    }

    // Получение всех заказов
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Получение заказа по ID
    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Заказ не найден"));
    }

    // Получение заказов покупателя
    @Transactional(readOnly = true)
    public List<Order> getOrdersByBuyer(Long buyerId) {
        return orderRepository.findByBuyerId(buyerId);
    }

    // Получение заказов продавца
    @Transactional(readOnly = true)
    public List<Order> getOrdersBySeller(Long sellerId) {
        return orderRepository.findBySellerId(sellerId);
    }

    // Оплата заказа
    @Transactional
    public Order payOrder(Long orderId, Long buyerId) {

        Order order = getOrderById(orderId);

        if (!order.getBuyer().getId().equals(buyerId)) {
            throw new IllegalStateException(
                    "Вы не можете оплачивать чужой заказ");
        }

        if (order.getStatus() != OrderStatus.NEW) {
            throw new IllegalStateException(
                    "Этот заказ нельзя оплатить");
        }

        User buyer = order.getBuyer();

        if (buyer.getBalance().compareTo(order.getPrice()) < 0) {
            throw new IllegalStateException(
                    "Недостаточно средств на балансе");
        }

        buyer.setBalance(
                buyer.getBalance().subtract(order.getPrice())
        );

        userRepository.save(buyer);

        order.setStatus(OrderStatus.PAID);

        return orderRepository.save(order);
    }

    // Подтверждение доставки товара
    @Transactional
    public Order deliverOrder(Long orderId, Long sellerId) {

        Order order = getOrderById(orderId);

        if (!order.getSeller().getId().equals(sellerId)) {
            throw new IllegalStateException(
                    "Вы не являетесь продавцом этого заказа");
        }

        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException(
                    "Заказ ещё не оплачен");
        }

        User seller = order.getSeller();

        seller.setBalance(
                seller.getBalance().add(order.getPrice())
        );

        userRepository.save(seller);

        order.setStatus(OrderStatus.DELIVERED);

        return orderRepository.save(order);
    }

    // Отмена заказа
    @Transactional
    public Order cancelOrder(Long orderId, Long userId) {

        Order order = getOrderById(orderId);

        boolean isBuyer =
                order.getBuyer().getId().equals(userId);

        boolean isSeller =
                order.getSeller().getId().equals(userId);

        if (!isBuyer && !isSeller) {
            throw new IllegalStateException(
                    "Вы не можете отменить чужой заказ");
        }

        if (order.getStatus() != OrderStatus.NEW) {
            throw new IllegalStateException(
                    "Можно отменить только новый заказ");
        }

        Item item = order.getItem();

        item.setAvailable(true);

        itemRepository.save(item);

        order.setStatus(OrderStatus.CANCELLED);

        return orderRepository.save(order);
    }

    // Открытие спора по заказу
    @Transactional
    public Order openDispute(Long orderId, Long buyerId) {

        Order order = getOrderById(orderId);

        if (!order.getBuyer().getId().equals(buyerId)) {
            throw new IllegalStateException(
                    "Только покупатель может открыть спор");
        }

        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException(
                    "Спор можно открыть только по оплаченному заказу");
        }

        order.setStatus(OrderStatus.DISPUTE);

        return orderRepository.save(order);
    }
}