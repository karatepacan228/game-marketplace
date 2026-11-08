
package com.example.demo.controller;

import com.example.demo.entity.Game;
import com.example.demo.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    // Получение всех игр
    @GetMapping
    public List<Game> getAllGames() {
        return gameService.getAllGames();
    }

    // Получение игры по ID
    @GetMapping("/{id}")
    public Game getGameById(@PathVariable Long id) {
        return gameService.getGameById(id);
    }

    // Добавление новой игры
    @PostMapping
    public Game createGame(@RequestBody Game game) {
        return gameService.createGame(game);
    }

    // Изменение игры
    @PutMapping("/{id}")
    public Game updateGame(
            @PathVariable Long id,
            @RequestBody Game game) {
        return gameService.updateGame(id, game);
    }

    // Удаление игры
    @DeleteMapping("/{id}")
    public void deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
    }
}
