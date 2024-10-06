package de.tschoooons.deck_ranking_server.dtos;

import java.util.Date;
import java.util.Map;
import java.util.List;

import lombok.Data;

@Data
public class RegisterGameDto {
    private Date playedAt;
    private String comment;
    private int participants;

    // Maps which deck got what place in this game
    // (deck_id) -> (placement)
    private Map<Integer, Integer> placements;

    // List of pod_ids the new game shall belong to.
    private List<Long> pods;
}
