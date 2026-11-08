
package com.example.demo.service;

import com.example.demo.dto.ChatMessageResponse;
import com.example.demo.entity.ChatMessage;
import com.example.demo.entity.Item;
import com.example.demo.entity.User;
import com.example.demo.repository.ChatRepository;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.ChatConversationResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    // Отправка сообщения
    @Transactional
    public ChatMessageResponse sendMessage(
            String username,
            Long itemId,
            Long buyerId,
            String content
    ) {
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("Пользователь не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Товар не найден"));

        User seller = item.getSeller();

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Сообщение не может быть пустым");
        }

        if (content.length() > 2000) {
            throw new IllegalArgumentException("Сообщение слишком длинное");
        }

        User buyer;

        // Если пишет продавец
        if (sender.getId().equals(seller.getId())) {

            if (buyerId == null) {
                throw new IllegalArgumentException(
                        "Необходимо указать покупателя");
            }

            buyer = userRepository.findById(buyerId)
                    .orElseThrow(() ->
                            new IllegalArgumentException("Покупатель не найден"));

            // Продавец может отвечать только в существующей переписке
            if (chatRepository.findConversation(
                    itemId, buyer.getId(), seller.getId()
            ).isEmpty()) {
                throw new IllegalArgumentException(
                        "Переписка с этим покупателем не найдена");
            }

        } else {
            // Если пишет покупатель
            buyer = sender;

            if (buyerId != null && !buyerId.equals(sender.getId())) {
                throw new IllegalArgumentException(
                        "Нельзя отправлять сообщение от имени другого покупателя");
            }
        }

        ChatMessage message = new ChatMessage();
        message.setItem(item);
        message.setBuyer(buyer);
        message.setSeller(seller);
        message.setSender(sender);
        message.setContent(content.trim());

        return ChatMessageResponse.from(chatRepository.save(message));
    }

    // Получение истории переписки
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getConversation(
            String username,
            Long itemId,
            Long buyerId
    ) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("Пользователь не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Товар не найден"));

        User seller = item.getSeller();

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Покупатель не найден"));

        // Читать переписку могут только её участники
        if (!user.getId().equals(seller.getId())
                && !user.getId().equals(buyer.getId())) {
            throw new IllegalArgumentException(
                    "Нет доступа к этой переписке");
        }

        return chatRepository.findConversation(
                        itemId,
                        buyer.getId(),
                        seller.getId()
                )
                .stream()
                .map(ChatMessageResponse::from)
                .toList();
    }
    // Получение списка диалогов пользователя
@Transactional(readOnly = true)
public List<ChatConversationResponse> getConversations(String username) {

    User user = userRepository.findByUsername(username)
            .orElseThrow(() ->
                    new IllegalArgumentException("Пользователь не найден"));

    List<ChatMessage> messages =
            chatRepository.findUserMessages(user.getId());

    // Группируем сообщения по товару и покупателю
    Map<String, ChatMessage> conversations = new LinkedHashMap<>();

    for (ChatMessage message : messages) {

        String key = message.getItem().getId()
                + "_" + message.getBuyer().getId();

        // Сохраняем только последнее сообщение диалога
        conversations.putIfAbsent(key, message);
    }

    return conversations.values().stream()
            .map(message -> new ChatConversationResponse(
                    message.getItem().getId(),
                    message.getItem().getTitle(),
                    message.getBuyer().getId(),
                    message.getBuyer().getUsername(),
                    message.getSeller().getId(),
                    message.getSeller().getUsername(),
                    ChatMessageResponse.from(message)
            ))
            .toList();
}
}
