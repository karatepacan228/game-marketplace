
package com.example.demo.controller;

import com.example.demo.dto.ChatMessageResponse;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.ChatConversationResponse;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // Отправка сообщения
    @PostMapping("/items/{itemId}/messages")
    public ChatMessageResponse sendMessage(
            @PathVariable Long itemId,
            @RequestBody SendMessageRequest request,
            Authentication authentication
    ) {
        return chatService.sendMessage(
                authentication.getName(),
                itemId,
                request.buyerId(),
                request.content()
        );
    }

    // Получение истории переписки
    @GetMapping("/items/{itemId}/messages")
    public List<ChatMessageResponse> getConversation(
            @PathVariable Long itemId,
            @RequestParam Long buyerId,
            Authentication authentication
    ) {
        return chatService.getConversation(
                authentication.getName(),
                itemId,
                buyerId
        );
    }

    public record SendMessageRequest(
            Long buyerId,
            String content
    ) {}
    // Получение списка диалогов пользователя
@GetMapping("/conversations")
public List<ChatConversationResponse> getConversations(
        Authentication authentication
) {
    return chatService.getConversations(authentication.getName());
}
}
