package de.tschoooons.deck_ranking_server.entities;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "games")
@Entity
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private long id;

    private Date playedAt;

    private String comment;

    private int participants;

    @OneToMany( mappedBy = "game", 
                fetch = FetchType.LAZY,
                cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
    private List<GamePlacement> placements;

    @OneToMany( mappedBy = "game",
                fetch = FetchType.LAZY,
                cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.REFRESH})
    private List<PodGame> podGames;

    public void addDeck(Deck deck, int position) {
        GamePlacement gamePlacement = new GamePlacement(this, deck, position);
        placements.add(gamePlacement);
    }
}
