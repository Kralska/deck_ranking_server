package de.tschoooons.deck_ranking_server.dtos;

import java.util.Date;
import java.util.Map;

import lombok.Data;

@Data
public class RegisterGameDto {
    
    private int participants;

    private Date playedAt;

    private String comment;

    // Maps which deck got what place in this game
    // (deck_id) -> (placement)
    private Map<Long, Integer> placements;

}
