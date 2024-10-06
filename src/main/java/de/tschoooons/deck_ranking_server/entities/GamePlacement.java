package de.tschoooons.deck_ranking_server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Basic;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "game_placements")
public class GamePlacement {
    @EmbeddedId
    @JsonIgnore
    private GamePlacementId id = new GamePlacementId();
    
    @ManyToOne
    @MapsId("game_id")
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne
    @MapsId("deck_id")
    @JoinColumn(name = "deck_id")
    private Deck deck;
    
    @Basic(optional = false)
    private int position;

    public GamePlacement(Game game, Deck deck, int position) {
        this.game = game;
        this.deck = deck;
        this.position = position;
    }
}
