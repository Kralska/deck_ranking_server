package de.tschoooons.deck_ranking_server.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tschoooons.deck_ranking_server.dtos.DeckDto;
import de.tschoooons.deck_ranking_server.dtos.GameDto;
import de.tschoooons.deck_ranking_server.entities.Deck;
import de.tschoooons.deck_ranking_server.entities.Game;
import de.tschoooons.deck_ranking_server.services.DeckService;
import de.tschoooons.deck_ranking_server.services.GameService;
import de.tschoooons.deck_ranking_server.services.Mapper;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RequestMapping("api/decks")
@RestController
public class DeckController {

    private final DeckService deckService;
    private final GameService gameService;

    public DeckController(
        DeckService deckService,
        GameService gameService) {
        this.deckService = deckService;
        this.gameService = gameService;
    }

    @GetMapping("/{id}")
    public DeckDto getDeck(@PathVariable long id) {
        Deck deck = deckService.getByIdLoadLazyFetches(id);
        return Mapper.toDto(deck);
    }

    @GetMapping("")
    public List<DeckDto> allDecks() {
        List<DeckDto> allDecks = deckService.getAllDecks().stream()
            .map(Mapper::toDto).toList();
        //allDecks.sort(Comparator.comparing(DeckDto::getRating).reversed());
        return allDecks;
    }

    @PostMapping("")
    public DeckDto registerDeck(@Valid @RequestBody DeckDto deckDto) {
        Deck deck = deckService.register(deckDto);
        return Mapper.toDto(deck);
    }
    
    @PutMapping("/{id}")
    public DeckDto updateDeck(@PathVariable long id, @Valid @RequestBody DeckDto deckDto) {
        Deck deck = deckService.update(id, deckDto);
        return Mapper.toDto(deck);
    }

    @PatchMapping("/{id}")
    public DeckDto updateDeckPartial(@PathVariable long id, @RequestBody DeckDto updates) {
        Deck deck = deckService.update(id, updates);
        return Mapper.toDto(deck);
    }
    
    @DeleteMapping("/{id}")
    public void deleteDeck(@PathVariable long id) {
        deckService.delete(id);
    }

    // Games
    /**
     * Returns all games, that the deck with {@code id} participated in.
     * @param id Id of the deck.
     * @return List of games as {@link GameDto}.
     */
    @GetMapping("/{id}/games")
    public List<GameDto> getGamesForDeck(@PathVariable long id) {
        Deck deck = deckService.getById(id);
        List<Game> games = gameService.getGamesFrom(deck);
        return games.stream().map(Mapper::toDto).toList();
    }
}
