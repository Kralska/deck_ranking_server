package de.tschoooons.deck_ranking_server.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.tschoooons.deck_ranking_server.entities.PodParticipant;
import de.tschoooons.deck_ranking_server.entities.PodParticipantId;

@Repository
public interface PodParticipantRepository extends CrudRepository<PodParticipant, PodParticipantId>{

}
