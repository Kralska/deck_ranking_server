package de.tschoooons.deck_ranking_server.services;

import de.tschoooons.deck_ranking_server.repositories.DeckRepository;
import jakarta.transaction.Transactional;
import de.tschoooons.deck_ranking_server.dtos.DeckDto;
import de.tschoooons.deck_ranking_server.entities.Deck;
import de.tschoooons.deck_ranking_server.errors.EntityNotInDBException;

import java.util.List;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DeckService {
    private final DeckRepository deckRepository;

    public DeckService(DeckRepository deckRepository) {
        this.deckRepository = deckRepository;
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
        return deck;
    }

    public List<Deck> getAllDecks() {
        ArrayList<Deck> decks = new ArrayList<>();
        deckRepository.findAll().forEach(decks::add);
        return decks;
    }

    /*
     * Updates the deck with the id {id}.
     * 
     * Fields of deckDto that are null are ignored (will not be updated).
     */
    public Deck update(long id, DeckDto deckDto) {
        Deck updatedDeck = getById(id);
        if(deckDto.getName() != null) {
            updatedDeck.setName(deckDto.getName());
        }
        if(deckDto.getCommander() != null) {
            updatedDeck.setCommander(deckDto.getCommander());
        }
        return deckRepository.save(updatedDeck);
    }

    public Deck register(DeckDto deckDto)
    {
        Deck newDeck = new Deck();

        newDeck.setName(deckDto.getName());
        newDeck.setCommander(deckDto.getCommander());
        int rating = deckDto.getRating();
        newDeck.setRating(rating);

        return deckRepository.save(newDeck);
    }

    public void delete(long id) {
        deckRepository.deleteById(id);
    }

}
