package de.tschoooons.deck_ranking_server.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.tschoooons.deck_ranking_server.entities.Pod;

@Repository
public interface PodRepository extends CrudRepository<Pod, Long> {
}
