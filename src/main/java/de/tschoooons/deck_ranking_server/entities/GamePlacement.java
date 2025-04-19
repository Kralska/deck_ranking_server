package de.tschoooons.deck_ranking_server.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

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
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    private Game game;

    @ManyToOne
    @MapsId("deck_id")
    @JoinColumn(name = "deck_id")
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    private Deck deck;
    
    @Basic(optional = false)
    private int position;

    public GamePlacement(Game game, Deck deck, int position) {
        this.game = game;
        this.deck = deck;
        this.position = position;
    }
}
