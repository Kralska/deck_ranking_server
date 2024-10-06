package de.tschoooons.deck_ranking_server.services;

import de.tschoooons.deck_ranking_server.repositories.DeckRepository;
import jakarta.transaction.Transactional;
import de.tschoooons.deck_ranking_server.dtos.RegisterDeckDto;
import de.tschoooons.deck_ranking_server.entities.Deck;
import de.tschoooons.deck_ranking_server.entities.DeckRating;
import de.tschoooons.deck_ranking_server.entities.Game;
import de.tschoooons.deck_ranking_server.entities.GamePlacement;
import de.tschoooons.deck_ranking_server.entities.Pod;
import de.tschoooons.deck_ranking_server.entities.User;
import de.tschoooons.deck_ranking_server.errors.EntityNotInDBException;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DeckService {
    private final DeckRepository deckRepository;
    private final UserService userService;
    private final PodService podService;
    private final GameService gameService;

    public DeckService(DeckRepository deckRepository, UserService userService, PodService podService, GameService gameService) {
        this.deckRepository = deckRepository;
        this.userService = userService;
        this.podService = podService;
        this.gameService = gameService;
    }

    public Deck getById(long id)
    throws EntityNotInDBException
    {
        return deckRepository.findById(id)
            .orElseThrow(() -> new EntityNotInDBException("No Deck with id " + id + " found."));
    }

    public Deck getByIdLoadLazyFetches(long id)
    {
        Deck deck = getById(id);
        if(deck == null){
            return null;
        }
        Hibernate.initialize(deck.getPlacements());
        Hibernate.initialize(deck.getDeck_ratings());
        return deck;
    }

    public List<Deck> allDecks() {
        ArrayList<Deck> decks = new ArrayList<>();
        deckRepository.findAll().forEach(decks::add);
        return decks;
    }

    /*
     * Updates the deck with the id {id}.
     * 
     * Fields of registerDeckDto that are null will not be updated.
     */
    public Deck update(long id, RegisterDeckDto registerDeckDto) {
        Deck updatedDeck = getById(id);

        if(registerDeckDto.getOwnerId() != null) {
            User owner = userService.getById(registerDeckDto.getOwnerId());
            updatedDeck.setOwner(owner);
        }
        if(registerDeckDto.getName() != null) {
            updatedDeck.setName(registerDeckDto.getName());
        }
        if(registerDeckDto.getCommander() != null) {
            updatedDeck.setCommander(registerDeckDto.getCommander());
        }

        if(registerDeckDto.getPods() != null) {
            setDeckRatingFromDto(updatedDeck, registerDeckDto);
        }
        if(registerDeckDto.getPlacements() != null) {
            setDeckPlacementsFromDto(updatedDeck, registerDeckDto);
        }

        return deckRepository.save(updatedDeck);
    }

    public Deck register(RegisterDeckDto registerDeckDto)
    {
        Deck newDeck = new Deck();

        User owner = userService.getById(registerDeckDto.getOwnerId());
        newDeck.setOwner(owner);
        newDeck.setName(registerDeckDto.getName());
        newDeck.setCommander(registerDeckDto.getCommander());

        setDeckRatingFromDto(newDeck, registerDeckDto);
        setDeckPlacementsFromDto(newDeck, registerDeckDto);

        return deckRepository.save(newDeck);
    }

    public void delete(long id) {
        deckRepository.deleteById(id);
    }

    private void setDeckRatingFromDto(Deck deck, RegisterDeckDto dto) {
        deck.getDeck_ratings().clear();
        for(Long podId : dto.getPods()) {
            Pod pod = podService.getById(podId.longValue());
            DeckRating deckRating = new DeckRating(pod, deck, 1000);
            deck.getDeck_ratings().add(deckRating);
        }
    }

    private void setDeckPlacementsFromDto(Deck deck, RegisterDeckDto dto) {
        deck.getPlacements().clear();
        // GameID -> position
        for(Map.Entry<Long, Integer> entry: dto.getPlacements().entrySet()) {
            long gameId = entry.getKey().longValue();
            int position = entry.getValue().intValue();

            Game game = gameService.getById(gameId);
            GamePlacement gamePlacement = new GamePlacement(game, deck, position);
            deck.getPlacements().add(gamePlacement);
        }
    }
}
