package com.example.demo.service;

import com.example.demo.entity.Game;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final ItemRepository itemRepository;

    // Добавление новой игры
    @Transactional
    public Game createGame(Game game) {

        if (gameRepository.existsByName(game.getName())) {
            throw new IllegalArgumentException("Такая игра уже существует");
        }

        return gameRepository.save(game);
    }

    // Получение всех игр
    @Transactional(readOnly = true)
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    // Получение игры по ID
    @Transactional(readOnly = true)
    public Game getGameById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Игра не найдена"));
    }

    // Обновление информации об игре
    @Transactional
    public Game updateGame(Long id, Game updatedGame) {

        Game game = getGameById(id);

        game.setName(updatedGame.getName());
        game.setDescription(updatedGame.getDescription());

        return gameRepository.save(game);
    }

    // Удаление игры
    @Transactional
    public void deleteGame(Long id) {

        Game game = getGameById(id);

        if (itemRepository.existsByGame_Id(id)) {
            throw new IllegalStateException(
                    "Нельзя удалить игру, у которой есть товары");
        }

        gameRepository.delete(game);
    }
}