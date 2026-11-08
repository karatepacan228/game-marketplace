package com.example.demo.repository;

import com.example.demo.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<ChatMessage, Long> {

    @Query("""
            SELECT m FROM ChatMessage m
            WHERE m.item.id = :itemId
            AND m.buyer.id = :buyerId
            AND m.seller.id = :sellerId
            ORDER BY m.sentAt ASC
            """)
    List<ChatMessage> findConversation(
            @Param("itemId") Long itemId,
            @Param("buyerId") Long buyerId,
            @Param("sellerId") Long sellerId
    );

    @Query("""
            SELECT m FROM ChatMessage m
            WHERE m.buyer.id = :userId
            OR m.seller.id = :userId
            ORDER BY m.sentAt DESC
            """)
    List<ChatMessage> findUserMessages(@Param("userId") Long userId);
}