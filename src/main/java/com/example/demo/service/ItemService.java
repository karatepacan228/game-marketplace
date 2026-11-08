package com.example.demo.service;

import com.example.demo.entity.Game;
import com.example.demo.entity.Item;
import com.example.demo.entity.User;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    // Создание нового товара
    @Transactional
    public Item createItem(
            String title,
            String description,
            BigDecimal price,
            Long sellerId,
            Long gameId) {

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Цена должна быть больше нуля");
        }

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Продавец не найден"));

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Игра не найдена"));

        Item item = new Item();

        item.setTitle(title);
        item.setDescription(description);
        item.setPrice(price);
        item.setSeller(seller);
        item.setGame(game);
        item.setAvailable(true);

        return itemRepository.save(item);
    }

    // Получение всех товаров
    @Transactional(readOnly = true)
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // Получение доступных товаров
    @Transactional(readOnly = true)
    public List<Item> getAvailableItems() {
        return itemRepository.findByAvailableTrue();
    }

    // Получение товара по ID
    @Transactional(readOnly = true)
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Товар не найден"));
    }

    // Получение товаров конкретного продавца
    @Transactional(readOnly = true)
    public List<Item> getItemsBySeller(Long sellerId) {
        return itemRepository.findBySellerId(sellerId);
    }

    // Изменение товара
    @Transactional
    public Item updateItem(
            Long itemId,
            Long sellerId,
            String title,
            String description,
            BigDecimal price) {

        Item item = getItemById(itemId);

        if (!item.getSeller().getId().equals(sellerId)) {
            throw new IllegalStateException(
                    "Вы не можете изменять чужой товар");
        }

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Некорректная цена");
        }

        if (!item.isAvailable()) {
            throw new IllegalStateException(
                    "Нельзя изменять недоступный товар");
        }

        item.setTitle(title);
        item.setDescription(description);
        item.setPrice(price);

        return itemRepository.save(item);
    }

    // Удаление товара
    @Transactional
    public void deleteItem(Long itemId, Long sellerId) {

        Item item = getItemById(itemId);

        if (!item.getSeller().getId().equals(sellerId)) {
            throw new IllegalStateException(
                    "Вы не можете удалить чужой товар");
        }

        itemRepository.delete(item);
    }
}