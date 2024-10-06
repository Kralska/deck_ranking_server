package de.tschoooons.deck_ranking_server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "pod_games")
public class PodGame {
    @EmbeddedId
    @JsonIgnore
    private PodGameId id = new PodGameId();

    @ManyToOne
    @MapsId("game_id")
    @JoinColumn(name = "game_id")
    @EqualsAndHashCode.Include
    private Game game;

    @ManyToOne
    @MapsId("pod_id")
    @JoinColumn(name = "pod_id")
    @EqualsAndHashCode.Include
    private Pod pod;

    public PodGame(Pod pod, Game game) {
        this.pod = pod;
        this.game = game;
    }

}
