
package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Регистрация нового пользователя
    @Transactional
    public User register(String username, String email, String password) {

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Такой логин уже занят");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Такой email уже занят");
        }

        User user = new User();

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(Role.BUYER);
        user.setBalance(BigDecimal.ZERO);

        return userRepository.save(user);
    }

    // Получение всех пользователей
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Получение пользователя по ID
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Пользователь не найден"));
    }

    // Поиск пользователя по логину
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("Пользователь не найден"));
    }

    // Пополнение баланса покупателя администратором
    @Transactional
    public User depositBalance(Long adminId, Long buyerId, BigDecimal amount) {

        // Проверяем администратора
        User admin = getUserById(adminId);

        if (admin.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException(
                    "Недостаточно прав. Только администратор может пополнять баланс");
        }

        // Проверяем покупателя
        User buyer = getUserById(buyerId);

        if (buyer.getRole() != Role.BUYER) {
            throw new IllegalArgumentException(
                    "Пополнять можно только баланс покупателя");
        }

        // Проверяем сумму
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Сумма пополнения должна быть больше нуля");
        }

        // Увеличиваем баланс
        buyer.setBalance(buyer.getBalance().add(amount));

        return userRepository.save(buyer);
    }
}
