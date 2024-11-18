package de.tschoooons.deck_ranking_server.services;

import de.tschoooons.deck_ranking_server.repositories.DeckRepository;
import de.tschoooons.deck_ranking_server.repositories.GameRepository;
import de.tschoooons.deck_ranking_server.repositories.PodRepository;
import de.tschoooons.deck_ranking_server.repositories.UserRepository;
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
    private final UserRepository userRepository;
    private final PodRepository podRepository;
    private final GameRepository gameRepository;

    public DeckService(DeckRepository deckRepository, UserRepository userRepository, PodRepository podRepository, GameRepository gameRepository) {
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
        this.podRepository = podRepository;
        this.gameRepository = gameRepository;
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

    public List<Deck> getAllDecks() {
        ArrayList<Deck> decks = new ArrayList<>();
        deckRepository.findAll().forEach(decks::add);
        return decks;
    }

    public List<Deck> getAllDecksOwnedBy(long ownerId) {
        ArrayList<Deck> decks = new ArrayList<>();
        deckRepository.findByOwnerIdIn(List.of(ownerId)).forEach(decks::add);
        return decks;
    }

    /*
     * Updates the deck with the id {id}.
     * 
     * Fields of deckDto that are null are ignored (will not be updated).
     */
    public Deck update(long id, RegisterDeckDto deckDto) {
        Deck updatedDeck = getById(id);

        if(deckDto.getOwner() != null) {
            User owner = userRepository.findById(deckDto.getOwner()).get();
            updatedDeck.setOwner(owner);
        }
        if(deckDto.getName() != null) {
            updatedDeck.setName(deckDto.getName());
        }
        if(deckDto.getCommander() != null) {
            updatedDeck.setCommander(deckDto.getCommander());
        }

        if(deckDto.getPods() != null) {
            setDeckRatingFromDto(updatedDeck, deckDto);
        }
        if(deckDto.getPlacements() != null) {
            setDeckPlacementsFromDto(updatedDeck, deckDto);
        }

        return deckRepository.save(updatedDeck);
    }

    public Deck register(RegisterDeckDto registerDeckDto)
    {
        Deck newDeck = new Deck();

        User owner = userRepository.findById(registerDeckDto.getOwner()).get();
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
        if(dto.getPods() != null) {
            for(Long podId : dto.getPods()) {
                Pod pod = podRepository.findById(podId.longValue()).get();
                DeckRating deckRating = new DeckRating(pod, deck, 1000);
                deck.getDeck_ratings().add(deckRating);
            }
        }
    }

    private void setDeckPlacementsFromDto(Deck deck, RegisterDeckDto dto) {
        deck.getPlacements().clear();
        // GameID -> position
        if(dto.getPlacements() != null){
            for(Map.Entry<Long, Integer> entry: dto.getPlacements().entrySet()) {
                long gameId = entry.getKey().longValue();
                int position = entry.getValue().intValue();
    
                Game game = gameRepository.findById(gameId).get();
                GamePlacement gamePlacement = new GamePlacement(game, deck, position);
                deck.getPlacements().add(gamePlacement);
            }
        }
    }
}
