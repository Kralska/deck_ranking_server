package de.tschoooons.deck_ranking_server.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

public class TestEloService {

    @InjectMocks
    private EloService eloService = new EloService();

    private int maxDifference = 400;
    private int kFactor = 40;

    @Test
    public void CorrectRatingChangeIn1v1() {
        // Win
        int ratingChange = eloService.ratingChange1v1(1000, 1000, EloService.GameResult.WIN, maxDifference, kFactor);
        assertEquals(20, ratingChange);

        // Loss
        ratingChange = eloService.ratingChange1v1(1000, 1000, EloService.GameResult.DRAW, maxDifference, kFactor);
        assertEquals(0, ratingChange);

        // Draw
        ratingChange = eloService.ratingChange1v1(1000, 1000, EloService.GameResult.LOSS, maxDifference, kFactor);
        assertEquals(-20, ratingChange);
    }

    @Test 
    public void CorrectRatingChangeWithMultipleOpponents() {
        int ratingChange = eloService.ratingChange(1000, 1200, 3.5f, 4, maxDifference, kFactor);
        assertEquals(102, ratingChange);

        ratingChange = eloService.ratingChange(971, 891, 0.5f, 5, maxDifference, kFactor);
        assertEquals(-103, ratingChange);

        ratingChange = eloService.ratingChange(927,1287, 8f, 8, maxDifference, kFactor);
        assertEquals(284, ratingChange);

        ratingChange = eloService.ratingChange(1094, 1143, 4f, 9, maxDifference, kFactor);
        assertEquals(5, ratingChange);

    }

}
