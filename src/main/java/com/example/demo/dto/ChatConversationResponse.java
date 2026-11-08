package com.example.demo.dto;

public record ChatConversationResponse(
        Long itemId,
        String itemTitle,
        Long buyerId,
        String buyerUsername,
        Long sellerId,
        String sellerUsername,
        ChatMessageResponse lastMessage
) {
}