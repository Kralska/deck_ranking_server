package de.tschoooons.deck_ranking_server.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tschoooons.deck_ranking_server.dtos.DeckDto;
import de.tschoooons.deck_ranking_server.dtos.RegisterDeckDto;
import de.tschoooons.deck_ranking_server.entities.Deck;
import de.tschoooons.deck_ranking_server.services.DeckService;
import de.tschoooons.deck_ranking_server.services.Mapper;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RequestMapping("api/decks")
@RestController
@CrossOrigin
public class DeckController {

    private final DeckService deckService;

    public DeckController(DeckService deckService) {
        this.deckService = deckService;
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
        allDecks.sort(Comparator.comparing(DeckDto::getRating).reversed());
        return allDecks;
    }

    @PostMapping("")
    public DeckDto registerDeck(@Valid @RequestBody RegisterDeckDto registerDeckDto) {
        Deck deck = deckService.register(registerDeckDto);
        return Mapper.toDto(deck);
    }
    
    @PutMapping("/{id}")
    public DeckDto updateDeck(@PathVariable long id, @Valid @RequestBody RegisterDeckDto updatedDeck) {
        // Put in an empty map for placements instead of null to ensure removal
        if(updatedDeck.getPlacements() == null) {
            updatedDeck.setPlacements(new HashMap<>());
        }
        // Put in an empty map for pods instead of null to ensure removal
        if(updatedDeck.getPods() == null) {
            updatedDeck.setPods(new HashSet<>());
        }

        Deck deck = deckService.update(id, updatedDeck);
        return Mapper.toDto(deck);
    }

    @PatchMapping("/{id}")
    public DeckDto updateDeckPartial(@PathVariable long id, @RequestBody RegisterDeckDto updates) {
        Deck deck = deckService.update(id, updates);
        return Mapper.toDto(deck);
    }
    
    @DeleteMapping("/{id}")
    public void deleteDeck(@PathVariable long id) {
        deckService.delete(id);
    }
}
