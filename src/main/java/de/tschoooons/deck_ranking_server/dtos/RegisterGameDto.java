package de.tschoooons.deck_ranking_server.dtos;

import java.util.Date;
import java.util.Map;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

import lombok.Data;

@Data
public class RegisterGameDto {
    
    @NotNull
    private int participants;

    private Date playedAt;

    @Size(max = 10000)
    private String comment;

    // Maps which deck got what place in this game
    // (deck_id) -> (placement)
    private Map<Long, Integer> placements;

    // List of pod_ids the new game shall belong to.
    private List<Long> pods;
}
