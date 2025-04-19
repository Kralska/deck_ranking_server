package de.tschoooons.deck_ranking_server.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tschoooons.deck_ranking_server.dtos.GameDto;
import de.tschoooons.deck_ranking_server.dtos.RegisterGameDto;
import de.tschoooons.deck_ranking_server.entities.Game;
import de.tschoooons.deck_ranking_server.services.GameService;
import de.tschoooons.deck_ranking_server.services.Mapper;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin
public class GameController {
    private GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/{id}")
    public GameDto getGame(@PathVariable long id) {
        Game game = gameService.getByIdLoadLazyFetches(id);
        return Mapper.toDto(game);
    }

    @GetMapping(value = {"", "/"})
    public List<GameDto> allGames() {
        List<GameDto> games = gameService.allGames().stream()
            .map(Mapper::toDto).toList();
        return games;
    }
    
    @PostMapping(value = {"", "/", "register"})
    public GameDto registerGame(@RequestBody RegisterGameDto registerGameDto) {
        Game game = gameService.register(registerGameDto);
        return Mapper.toDto(game);
    }

    @PutMapping(value = "/{id}")
    public GameDto updateGame(@PathVariable long id, @Valid @RequestBody RegisterGameDto gameDto) {
        if(gameDto.getPlacements() == null) {
            gameDto.setPlacements(new HashMap<>());
        }
        if(gameDto.getPods() == null) {
            gameDto.setPods(new ArrayList<>());
        }
        Game updatedGame = gameService.update(id, gameDto);
        return Mapper.toDto(updatedGame);
    }

    @PatchMapping(value = "/{id}")
    public GameDto updateGamePartial(@PathVariable long id, @RequestBody RegisterGameDto gameDto) {
        Game updatedGame = gameService.update(id, gameDto);
        return Mapper.toDto(updatedGame);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteGame(@PathVariable long id) {
        gameService.delete(id);
    }
}
