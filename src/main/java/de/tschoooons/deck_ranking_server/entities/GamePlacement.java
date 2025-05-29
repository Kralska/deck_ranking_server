package de.tschoooons.deck_ranking_server.entities;

import jakarta.persistence.Basic;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "game_placements")
public class GamePlacement {
    @EmbeddedId
    private GamePlacementId id;
    
    @ManyToOne
    @MapsId("game_id")
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne
    @MapsId("deck_id")
    @JoinColumn(name = "deck_id")
    private Deck deck;

    @ManyToOne
    @MapsId("player_id")
    @JoinColumn(name = "player_id")
    private User player;
    
    @Basic(optional = false)
    private int position;

    /** The decks ELO-Rating <em>after</em> the game.*/
    @Basic
    private Integer rating;

    public GamePlacement(Game game, Deck deck, User player, int position) {
        this.game = game;
        this.deck = deck;
        this.position = position;
        this.player = player;
        id = new GamePlacementId(game.getId(), deck.getId(), player.getId());
    }
}
