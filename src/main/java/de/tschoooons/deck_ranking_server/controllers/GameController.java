package de.tschoooons.deck_ranking_server.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tschoooons.deck_ranking_server.dtos.GameDto;
import de.tschoooons.deck_ranking_server.entities.Game;
import de.tschoooons.deck_ranking_server.services.GameService;
import de.tschoooons.deck_ranking_server.services.Mapper;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@RequestMapping("api/games")
@RestController
public class GameController {
    private GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/{id}")
    public Optional<GameDto> getGame(@PathVariable long id) {
        Optional<Game> optGame = gameService.getById(id, true);
        if(optGame.isEmpty()){
            return Optional.empty();
        }
        return Optional.of(Mapper.toDto(optGame.get()));
    }

    @GetMapping(value = {"", "/"})
    public List<GameDto> allGames() {
        List<GameDto> games = gameService.allGames().stream()
            .map(Mapper::toDto).toList();
        return games;
    }
    
    @PostMapping(value = {"", "/", "register"})
    public GameDto registerGame(@RequestBody GameDto gameDto) {
        Game game = gameService.register(gameDto);
        return Mapper.toDto(game);
    }

    @PutMapping(value = "/{id}")
    public GameDto updateGame(@PathVariable long id, @Valid @RequestBody GameDto gameDto) {
        if(gameDto.getPlacements() == null) {
            gameDto.setPlacements(new ArrayList<>());
        }
        Game updatedGame = gameService.update(id, gameDto);
        return Mapper.toDto(updatedGame);
    }

    @PatchMapping(value = "/{id}")
    public GameDto updateGamePartial(@PathVariable long id, @RequestBody GameDto gameDto) {
        Game updatedGame = gameService.update(id, gameDto);
        return Mapper.toDto(updatedGame);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteGame(@PathVariable long id) {
        gameService.delete(id);
    }
}
