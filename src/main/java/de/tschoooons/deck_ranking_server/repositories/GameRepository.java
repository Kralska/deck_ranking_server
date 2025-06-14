package de.tschoooons.deck_ranking_server.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import de.tschoooons.deck_ranking_server.entities.Deck;
import de.tschoooons.deck_ranking_server.entities.Game;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {

    @Query("""
        SELECT game 
        FROM Game game 
        JOIN FETCH game.placements placements
        ORDER BY game.playedAt""")
    Iterable<Game> findAllWithPlacements();

    @Query("""
        select distinct placement.game
        from GamePlacement placement
        where placement.deck IN :decks
            """)
    Iterable<Game> findAllByDecks(@Param("decks") Iterable<Deck> decks);

}
