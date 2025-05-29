package de.tschoooons.deck_ranking_server.services;

import org.springframework.stereotype.Service;

import de.tschoooons.deck_ranking_server.entities.Deck;
import de.tschoooons.deck_ranking_server.entities.Game;
import de.tschoooons.deck_ranking_server.entities.GamePlacement;
import de.tschoooons.deck_ranking_server.entities.Pod;
import de.tschoooons.deck_ranking_server.entities.PodGame;
import de.tschoooons.deck_ranking_server.repositories.GameRepository;
import jakarta.transaction.Transactional;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.StreamSupport;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional
public class PodCalculationService {

    /** A decks starting elo before it plays any games */
    private final int STARTING_ELO = 1000;
    private final int MAX_DIFFERENCE = 1000;
    private final int K_FACTOR = 40;

    private EloService eloService;
    private DeckService deckService;
    private GameRepository gameRepository;

    public PodCalculationService(
        EloService eloService,
        DeckService deckService,
        GameRepository gameRepository ) {
        this.eloService = eloService;
        this.deckService = deckService;
        this.gameRepository = gameRepository;
    }

    /**
     * Calculates the ratings for all {@link Deck decks} and {@link Game games}. Updates database
     * entries.
     * 
     * <p> Tries to minimize calculations. Only recalculates games and ratings starting from the
     * earliest game with no calculated ratings.
     */
    public void calculateRatings() {
        List<Game> games = StreamSupport.stream(gameRepository.findAllWithPlacements().spliterator(), false).toList();
        if(games.isEmpty()){
            return;
        }
        // Reset deck rating if first game (with placements) isn't yet calculated
        {
            Iterator<Game> iter = games.iterator();
            while(iter.hasNext()){
                Game game = iter.next();
                if(game.getPlacements() == null || game.getPlacements().isEmpty()) {
                    continue;
                }
                if(isUnrated(game)){
                    resetRatings();
                }
                break;
            }
        }

        // Calculate Ratings
        Map<Deck, Integer> previousRatings = new HashMap<>();
        for(Game game : games){
            for(GamePlacement placement : game.getPlacements()){
                previousRatings.put(placement.getDeck(), placement.getDeck().getRating());
            }
        }
        
        for(Game game : games) {
            System.out.println(game.getPlayedAt());
        }

        Iterator<Game> iter = games.iterator();
        Game game = null;
        // Skip all ordered, calculated games
        while(iter.hasNext()){
            game = iter.next();
            if(isUnrated(game)){
                break;
            } else {
                for(GamePlacement placement : game.getPlacements()){
                    previousRatings.put(placement.getDeck(),placement.getRating());
                }
            }
        }
        if (game == null){
            throw new IllegalStateException("game (literally) CAN'T be null");
        }

        do {
            Map<Deck, Integer> ratingChanges = calculateRatingChanges(game, previousRatings);
            for(GamePlacement placement : game.getPlacements()){
                Deck deck = placement.getDeck();
                Integer newRating = previousRatings.get(deck) + ratingChanges.get(deck);
                deck.setRating(newRating);
                placement.setRating(newRating);
                previousRatings.put(deck, newRating);
            }
            if(iter.hasNext()){
                game = iter.next();
            }
        } while(iter.hasNext());
    }

    /**
     * Resets the ratings for all decks.
     */
    public void resetRatings() {
        for(Deck deck : deckService.getAllDecks()) {
            deck.setRating(STARTING_ELO);
        }
    }

    /**
     * Checks whether the elo has been calculated for a {@link Game}
     * 
     * <p> A game's elo has been calculated if the rating for all matches is not {@code null}
     * @param game game to check
     * @return {@code true}, <em>if</em> elo has not been calculated for {@code game}
     *    <br> {@code false}, otherwise
     */
    private static boolean isUnrated(Game game) {
        List<GamePlacement> placements = game.getPlacements();
        return  placements.stream().anyMatch(
            placement -> placement.getRating() == null
        );
    }

    public Pod CalculateRatingsForPod(Pod pod, int StartingElo) {
        pod.setRatingForAllDecks(STARTING_ELO);
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

    private Map<Deck, Integer> calculateRatingChanges(Game game, Map<Deck, Integer> previousRatings) {
        HashMap<Deck, Integer> ratingChanges = new HashMap<>();

        // We can calculate the individual average opponent rating from the total rating
        // (Subtract own rating, divide by number of opponents)
        int totalRating = previousRatings.values().stream().reduce(0, Integer::sum);

        HashMap<Deck, Float> performances = calculatePerformances(game);
        for(Entry<Deck, Float> entry : performances.entrySet()) {
            int averageOpponentRating = totalRating - previousRatings.get(entry.getKey());
            averageOpponentRating =  averageOpponentRating / game.getParticipants() - 1;
            int change = eloService.ratingChange(
                previousRatings.get(entry.getKey()),
                averageOpponentRating,
                entry.getValue(),
                game.getParticipants() - 1,
                MAX_DIFFERENCE,
                K_FACTOR);
            ratingChanges.put(entry.getKey(), change);
        }
        
        return ratingChanges;
    }

    public Map<Deck, Integer> CalculateRatingChangeFromGame(Pod pod, Game game) {
        HashMap<Deck, Integer> ratingChanges = new HashMap<Deck, Integer>();
    
        int maxDifference = 1000;
        int kFactor = 40;
        HashMap<Deck, Float> performances = calculatePerformances(game);
        for(Entry<Deck, Float> entry: performances.entrySet()) {
            int change = eloService.ratingChange(pod.getRatingForDeck(entry.getKey()), pod.getAverageRating(game), entry.getValue(), game.getParticipants() - 1, maxDifference, kFactor);
            ratingChanges.put(entry.getKey(), change);
        }

        return ratingChanges;
    }

    /**
     * Calculates the performance for all {@link Deck decks} in {@link Game game}.
     * 
     * <p> <em>Performance</em> is a rating for how good a deck "performed" in the game. It is a
     * number. The higher, the better. A deck gets 1 point for each deck that has a lower position,
     * 0.5 points for every deck with the same position and no points for decks with a higher
     * position.
     * @param game The game for which the performance is calculated.
     * @return Map from deck to performance in this game
     */
    protected HashMap<Deck, Float> calculatePerformances(Game game) {
        HashMap<Deck, Float> performances = new HashMap<Deck, Float>();       
        List<GamePlacement> placements = game.getPlacements();
        int participants = game.getParticipants();
        HashMap<Float, Float> tiePenalties = new HashMap<Float, Float>();

        for(GamePlacement placement : placements) {
            int position = placement.getPosition();
            float performance = participants - position;
            tiePenalties.put(
                performance,
                tiePenalties.containsKey(performance)
                 ?  tiePenalties.get(performance) + 0.5f
                 :  0.0f
            );

            performances.put(placement.getDeck(), performance);
        }
        performances.replaceAll((k, v) -> v - tiePenalties.get(v));

        return performances;
    }
    

}
