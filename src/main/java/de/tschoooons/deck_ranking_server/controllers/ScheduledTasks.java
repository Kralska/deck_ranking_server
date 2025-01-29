package de.tschoooons.deck_ranking_server.controllers;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.tschoooons.deck_ranking_server.services.PodCalculationService;

@Component
public class ScheduledTasks {

    @Autowired
    private PodCalculationService podCalculationService;

    @Scheduled(fixedRate = 60, timeUnit = TimeUnit.SECONDS)
    public void calculateRatings() {
        podCalculationService.CalculateRatings();
        System.out.println("Calculated Ratings!");
    }

}
