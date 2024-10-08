package de.tschoooons.deck_ranking_server.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.tschoooons.deck_ranking_server.entities.Deck;
import java.util.List;



@Repository
public interface DeckRepository extends CrudRepository<Deck, Long> {

    public List<Deck> findByOwnerIdIn(List<Long> ids);

}
