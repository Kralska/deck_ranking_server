package de.tschoooons.deck_ranking_server.entities;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@EqualsAndHashCode
@Table(name = "decks")
@Entity
public class Deck {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    @NotNull
    private long id;

    @NaturalId(mutable = true)
    @Basic(optional = false)
    @NotNull
    @Size(max = 500)
    private String name;

    @Size(max = 500)
    private String commander;

    @Basic(optional = false)
    private Integer rating = 1000;

}
