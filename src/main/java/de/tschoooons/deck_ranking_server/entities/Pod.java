package de.tschoooons.deck_ranking_server.entities;

import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MapKeyJoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "pods")
@Entity
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id")
public class Pod {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    @NaturalId( mutable = true)
    @EqualsAndHashCode.Include
    private String name;

    @OneToMany( mappedBy = "pod",
                fetch = FetchType.LAZY,
                cascade = CascadeType.ALL)
    @Column(name = "role")
    private List<PodParticipant> podParticipants = new ArrayList<>();    

    @OneToMany( mappedBy = "pod",
                fetch = FetchType.LAZY,
                cascade = CascadeType.ALL)
    @Column(name = "position")
    @MapKeyJoinColumn(name = "deck_id")
    private List<DeckRating> deckRatings = new ArrayList<>();

    @OneToMany( mappedBy = "pod", 
                fetch = FetchType.LAZY,
                cascade = CascadeType.ALL)
    private List<PodGame> podGames = new ArrayList<>();

    private static final int defaultStartingElo = 1000;

    public boolean containsGame(Game game) {
        for(PodGame podGame : podGames){
            if(podGame.getGame() == game){
                return true;
            }
        }
        return false;
    }

    public boolean containsDeck(Deck deck) {
        DeckRating searchFor = new DeckRating(this, deck);
        return deckRatings.contains(searchFor);
    }

    public void setRatingForDeck(Deck deck, int rating) {
        if(!containsDeck(deck)) {
            throw new IllegalArgumentException("Cant add deck to this pod, deck is not part of the pod!");
        }
        for(DeckRating deckRating : deckRatings) {
            if(deckRating.getDeck() == deck) {
                deckRating.setRating(rating);
            }
        }
    }

    public void setRatingForAllDecks(int rating) {
        for(DeckRating deckRating : deckRatings) {
            deckRating.setRating(rating);
        }
    }

    public Integer getRatingForDeck(Deck deck)  {
        for(DeckRating deckRating : deckRatings){
            if(deckRating.getDeck() == deck) {
                return deckRating.getRating();
            }
        }
        return null;
    }

    public int getAverageRating(Game game) {
        final int untrackedOpponentRating = 1000;
    
        int totalRating = 0;
        for(GamePlacement gamePlacement : game.getPlacements()) {
            if(containsDeck(gamePlacement.getDeck())){
                totalRating += getRatingForDeck(gamePlacement.getDeck());
            } else {
                totalRating += untrackedOpponentRating;
            }
        }
        return totalRating / game.getParticipants();
    }

    public void addDeck(Deck deck) {
        addDeck(deck, defaultStartingElo);
    }

    public void addDeck(Deck deck, int rating) {
        deckRatings.add(new DeckRating(this, deck, rating));
    }

    public void setDecks(Collection<Deck> decks, int rating ){
        if(deckRatings == null){
            deckRatings = new ArrayList<>();
        } else {
            deckRatings.clear();
        }
        for(Deck deck : decks) {
            addDeck(deck, rating);
        }
    }

    public void setDecks(Collection<Deck> decks) {
        setDecks(decks, defaultStartingElo);
    }

    public void addGame(Game game) {
        PodGame podGame = new PodGame(this, game);
        podGames.add(podGame);
    }

    public void setGames(Collection<Game> games) {
        podGames.clear();
        for(Game game : games) {
            addGame(game);
        }

    }

    public void addUser(User user, UserPodRole role) {
        PodParticipant podParticipant = new PodParticipant(this, user, role);
        podParticipants.add(podParticipant);
    }

    public void setUsers(Map<User, UserPodRole> users) {
        if(podParticipants == null){
            podParticipants = new ArrayList<>();
        } else {
            podParticipants.clear();
        }
    }
}
