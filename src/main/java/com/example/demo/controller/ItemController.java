
package com.example.demo.controller;

import com.example.demo.dto.ItemResponse;
import com.example.demo.entity.Item;
import com.example.demo.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // Получение всех товаров
    @GetMapping
    public List<ItemResponse> getAllItems() {
        return itemService.getAllItems()
                .stream()
                .map(ItemResponse::from)
                .toList();
    }

    // Получение доступных товаров
    @GetMapping("/available")
    public List<ItemResponse> getAvailableItems() {
        return itemService.getAvailableItems()
                .stream()
                .map(ItemResponse::from)
                .toList();
    }

    // Получение товара по ID
    @GetMapping("/{id}")
    public ItemResponse getItemById(@PathVariable Long id) {
        return ItemResponse.from(itemService.getItemById(id));
    }

    // Получение товаров конкретного продавца
    @GetMapping("/seller/{sellerId}")
    public List<ItemResponse> getItemsBySeller(
            @PathVariable Long sellerId) {
        return itemService.getItemsBySeller(sellerId)
                .stream()
                .map(ItemResponse::from)
                .toList();
    }

    // Создание товара
    @PostMapping
    public ItemResponse createItem(@RequestBody CreateItemRequest request) {
        Item item = itemService.createItem(
                request.title(),
                request.description(),
                request.price(),
                request.sellerId(),
                request.gameId()
        );

        return ItemResponse.from(item);
    }

    // Изменение товара
    @PutMapping("/{id}")
    public ItemResponse updateItem(
            @PathVariable Long id,
            @RequestBody UpdateItemRequest request) {

        Item item = itemService.updateItem(
                id,
                request.sellerId(),
                request.title(),
                request.description(),
                request.price()
        );

        return ItemResponse.from(item);
    }

    // Удаление товара
    @DeleteMapping("/{id}")
    public void deleteItem(
            @PathVariable Long id,
            @RequestParam Long sellerId) {
        itemService.deleteItem(id, sellerId);
    }

    // DTO для создания товара
    public record CreateItemRequest(
            String title,
            String description,
            BigDecimal price,
            Long sellerId,
            Long gameId
    ) {}

    // DTO для изменения товара
    public record UpdateItemRequest(
            String title,
            String description,
            BigDecimal price,
            Long sellerId
    ) {}
}
