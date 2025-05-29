package de.tschoooons.deck_ranking_server.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "games")
@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    private Date playedAt;

    private String comment;

    private int participants;

    @OneToMany( mappedBy = "game", 
                fetch = FetchType.EAGER,
                cascade = CascadeType.ALL)
    private List<GamePlacement> placements = new ArrayList<>();

    @OneToMany( mappedBy = "game",
                fetch = FetchType.LAZY,
                cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.REFRESH})
    private List<PodGame> podGames = new ArrayList<>();

    public void addDeck(Deck deck, User player, int position) {
        GamePlacement gamePlacement = new GamePlacement(this, deck, player, position);
        placements.add(gamePlacement);
    }
}
