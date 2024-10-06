package de.tschoooons.deck_ranking_server.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.tschoooons.deck_ranking_server.entities.GamePlacement;
import de.tschoooons.deck_ranking_server.entities.GamePlacementId;

@Repository
public interface GamePlacementRepository extends CrudRepository<GamePlacement, GamePlacementId>{

}
