package com.example.demo.repository;

import com.example.demo.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByAvailableTrue();

    List<Item> findBySellerId(Long sellerId);

    boolean existsByGame_Id(Long gameId);
}