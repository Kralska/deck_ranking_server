package de.tschoooons.deck_ranking_server.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import de.tschoooons.deck_ranking_server.entities.User;

@Repository
public interface UserRepository extends 
    CrudRepository<User, Long>, 
    QueryByExampleExecutor<User> {
}
