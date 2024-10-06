package de.tschoooons.deck_ranking_server.services;

import java.util.EnumMap;

import org.springframework.stereotype.Service;

@Service
public class EloService {

    public EloService() {
        resultToScore.put(GameResult.WIN, 1.0);
        resultToScore.put(GameResult.DRAW, 0.5);
        resultToScore.put(GameResult.LOSS, 0.0);
    }

    public enum  GameResult {
        WIN, DRAW, LOSS
    }

    private EnumMap<GameResult, Double> resultToScore = new EnumMap<GameResult, Double>(GameResult.class);

    public int ratingChange1v1(int rating_player, int rating_opponent, GameResult result, int maxDifference, int kFactor) {
        return (int) Math.round(kFactor * (resultToScore.get(result) - expectedScore(rating_player, rating_opponent, maxDifference)));
    }

    private double expectedScore(int rating_player, int rating_opponent, int maxDifference) {
        return 1.0 / (1.0 + (Math.pow(10, Double.valueOf(rating_opponent - rating_player) / maxDifference)));
    }

    public int ratingChange(int rating_player, int average_opponent_rating, float performance, int best_possible_performance, int maxDifference, int kFactor) {
        return (int) Math.round(kFactor * (performance - expectedScore(rating_player, average_opponent_rating, maxDifference) * best_possible_performance));
    }

}
