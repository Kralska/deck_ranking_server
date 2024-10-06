package de.tschoooons.deck_ranking_server.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tschoooons.deck_ranking_server.dtos.RegisterGameDto;
import de.tschoooons.deck_ranking_server.entities.Game;
import de.tschoooons.deck_ranking_server.services.GameService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@RequestMapping("api/games")
@RestController
public class GameController {
    private GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/{id}")
    public Game getGame(@PathVariable long id) {
        Game game = gameService.getByIdLoadLazyFetches(id);
        return game;
    }

    @GetMapping(value = {"", "/"})
    public List<Game> allGames() {
        List<Game> games = gameService.allGames();
        return games;
    }
    
    @PostMapping(value = {"", "/", "register"})
    public Game registerGame(@RequestBody RegisterGameDto registerGameDto) {
        Game game = gameService.register(registerGameDto);
        return game;
    }
}
