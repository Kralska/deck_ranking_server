package de.tschoooons.deck_ranking_server.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.tschoooons.deck_ranking_server.entities.DeckRating;
import de.tschoooons.deck_ranking_server.entities.DeckRatingId;

@Repository
public interface PodDeckRepository extends CrudRepository<DeckRating, DeckRatingId>{

}
