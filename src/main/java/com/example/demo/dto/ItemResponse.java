
package com.example.demo.dto;
import com.example.demo.entity.Item;
import java.math.BigDecimal;

public record ItemResponse(
        Long id,
        String title,
        String description,
        BigDecimal price,
        boolean available,
        Long sellerId,
        Long gameId
) {
    public static ItemResponse from(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getPrice(),
                item.isAvailable(),
                item.getSeller().getId(),
                item.getGame().getId()
        );
    }
}
