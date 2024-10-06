package de.tschoooons.deck_ranking_server.entities;

import org.hibernate.annotations.NaturalId;

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
@Table(name = "pod_decks")
public class DeckRating {
    @EmbeddedId
    @JsonIgnore
    private DeckRatingId id = new DeckRatingId();

    @ManyToOne
    @MapsId("pod_id")
    @NaturalId
    @JoinColumn(name = "pod_id")
    @EqualsAndHashCode.Include
    private Pod pod;

    @ManyToOne
    @MapsId("deck_id")
    @NaturalId
    @JoinColumn(name = "deck_id")
    @EqualsAndHashCode.Include
    private Deck deck;

    private Integer rating;

    public DeckRating(Pod pod, Deck deck) {
        this.pod = pod;
        this.deck = deck;
    }

    public DeckRating(Pod pod, Deck deck, int rating) {
        this(pod, deck);
        this.rating = rating;
    }
}
