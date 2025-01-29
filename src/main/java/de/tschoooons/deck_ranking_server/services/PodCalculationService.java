package de.tschoooons.deck_ranking_server.services;

import org.springframework.stereotype.Service;

import de.tschoooons.deck_ranking_server.entities.Deck;
import de.tschoooons.deck_ranking_server.entities.Game;
import de.tschoooons.deck_ranking_server.entities.GamePlacement;
import de.tschoooons.deck_ranking_server.entities.Pod;
import de.tschoooons.deck_ranking_server.entities.PodGame;
import jakarta.transaction.Transactional;

import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class PodCalculationService {

    private int defaultStartingElo = 1000;

    private EloService eloService;
    private DeckService deckService;
    private GameService gameService;

    public PodCalculationService(EloService eloService, DeckService deckService, GameService gameService) {
        this.eloService = eloService;
        this.deckService = deckService;
        this.gameService = gameService;
    }

    public void CalculateRatings() {
        for(Deck deck : this.deckService.getAllDecks()) {
            deck.setRating(defaultStartingElo);
        }
        for(Game game : gameService.allGames()) {
            Map<Deck, Integer> ratingChanges = this.CalculateRatingChangeFromGame(game);
            for(Entry<Deck, Integer> entry : ratingChanges.entrySet()) {
                int oldRating = entry.getKey().getRating();
                entry.getKey().setRating(oldRating + entry.getValue());
            }
        }
    }

    public Pod CalculateRatingsForPod(Pod pod, int StartingElo) {
        pod.setRatingForAllDecks(defaultStartingElo);
        for(PodGame podGame : pod.getPodGames()) {
            Game game = podGame.getGame();
            Map<Deck, Integer> ratingChanges = this.CalculateRatingChangeFromGame(pod, game);
            for(Entry<Deck, Integer> entry : ratingChanges.entrySet()) {
                int oldRating = pod.getRatingForDeck(entry.getKey());
                pod.setRatingForDeck(entry.getKey(), oldRating + entry.getValue());
            }
        }
        return pod;
    }

    public Map<Deck, Integer> CalculateRatingChangeFromGame(Game game) {
        HashMap<Deck, Integer> ratingChanges = new HashMap<>();

        int maxDifference = 1000;
        int kFactor = 40;
        // We can calculate the individual average opponent rating from the total rating
        // (Subtract own rating, divide by number of opponents)
        int totalRating = 0;
        for(GamePlacement placement : game.getPlacements()) {
            totalRating += placement.getDeck().getRating();
        }

        HashMap<Deck, Float> performances = CalculatePerformances(game);
        for(Entry<Deck, Float> entry : performances.entrySet()) {
            int averageOpponentRating = totalRating - entry.getKey().getRating();
            averageOpponentRating =  averageOpponentRating / game.getParticipants() - 1;
            int change = eloService.ratingChange(
                entry.getKey().getRating(),
                averageOpponentRating,
                entry.getValue(),
                game.getParticipants() - 1,
                maxDifference,
                kFactor);
            ratingChanges.put(entry.getKey(), change);
        }
        
        return ratingChanges;
    }

    public Map<Deck, Integer> CalculateRatingChangeFromGame(Pod pod, Game game) {
        HashMap<Deck, Integer> ratingChanges = new HashMap<Deck, Integer>();
    
        int maxDifference = 1000;
        int kFactor = 40;
        HashMap<Deck, Float> performances = CalculatePerformances(game);
        for(Entry<Deck, Float> entry: performances.entrySet()) {
            int change = eloService.ratingChange(pod.getRatingForDeck(entry.getKey()), pod.getAverageRating(game), entry.getValue(), game.getParticipants() - 1, maxDifference, kFactor);
            ratingChanges.put(entry.getKey(), change);
        }

        return ratingChanges;
    }

    public HashMap<Deck, Float> CalculatePerformances(Game game) {
        HashMap<Deck, Float> performances = new HashMap<Deck, Float>();       
        List<GamePlacement> placements = game.getPlacements();
        int participants = game.getParticipants();
        HashMap<Float, Float> tiePenalties = new HashMap<Float, Float>();

        for(int i = 0; i < placements.size(); ++i) {
            GamePlacement placement = placements.get(i);
            int position = placement.getPosition();
            float performance = participants - position;
            tiePenalties.put(
                performance,
                tiePenalties.containsKey(performance)
                 ?  tiePenalties.get(performance) + 0.5f
                 :  0.0f
            );

            performances.put(placements.get(i).getDeck(), performance);
        }
        performances.replaceAll((k, v) -> v - tiePenalties.get(v));

        return performances;
    }
    

}
