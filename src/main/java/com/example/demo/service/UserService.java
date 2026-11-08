
package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

        // Хешируем пароль перед сохранением
        user.setPassword(passwordEncoder.encode(password));

        // Каждый новый пользователь получает роль USER
        user.setRole(Role.USER);

        // Начальный баланс пользователя
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

    // Пополнение баланса пользователя администратором
    @Transactional
    public User depositBalance(Long adminId, Long userId, BigDecimal amount) {

        // Проверяем администратора
        User admin = getUserById(adminId);

        if (admin.getRole() != Role.ADMIN) {
           throw new AccessDeniedException("Недостаточно прав. Только администратор может пополнять баланс"); }

        // Находим пользователя
        User user = getUserById(userId);

        // Проверяем сумму пополнения
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Сумма пополнения должна быть больше нуля");
        }

        // Увеличиваем баланс пользователя
        user.setBalance(user.getBalance().add(amount));

        return userRepository.save(user);
    }
}
