package de.tschoooons.deck_ranking_server.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.tschoooons.deck_ranking_server.entities.PodGame;
import de.tschoooons.deck_ranking_server.entities.PodGameId;

@Repository
public interface PodGameRepository extends CrudRepository<PodGame, PodGameId>{

}
