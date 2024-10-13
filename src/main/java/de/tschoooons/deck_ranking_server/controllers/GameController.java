package de.tschoooons.deck_ranking_server.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tschoooons.deck_ranking_server.dtos.RegisterGameDto;
import de.tschoooons.deck_ranking_server.entities.Game;
import de.tschoooons.deck_ranking_server.services.GameService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;


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

    @PutMapping(value = "/{id}")
    public Game updateGame(@PathVariable long id, @Valid @RequestBody RegisterGameDto gameDto) {
        if(gameDto.getPlacements() == null) {
            gameDto.setPlacements(new HashMap<>());
        }
        if(gameDto.getPods() == null) {
            gameDto.setPods(new ArrayList<>());
        }
        return gameService.update(id, gameDto);
    }

    @PatchMapping(value = "/{id}")
    public Game updateGamePartial(@PathVariable long id, @RequestBody RegisterGameDto gameDto) {
        return gameService.update(id, gameDto);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteGame(@PathVariable long id) {
        gameService.delete(id);
    }
}
