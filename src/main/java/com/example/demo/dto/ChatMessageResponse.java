
package com.example.demo.dto;

import com.example.demo.entity.ChatMessage;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long id,
        Long itemId,
        Long buyerId,
        Long sellerId,
        Long senderId,
        String senderUsername,
        String content,
        LocalDateTime sentAt
) {

    public static ChatMessageResponse from(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getItem().getId(),
                message.getBuyer().getId(),
                message.getSeller().getId(),
                message.getSender().getId(),
                message.getSender().getUsername(),
                message.getContent(),
                message.getSentAt()
        );
    }
}
