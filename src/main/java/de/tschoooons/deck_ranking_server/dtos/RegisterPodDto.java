package de.tschoooons.deck_ranking_server.dtos;

import java.util.Map;
import java.util.Set;


import de.tschoooons.deck_ranking_server.entities.UserPodRole;
import lombok.Data;

@Data
public class RegisterPodDto {
    private String name;
    
    // Decks belonging to the pod
    // (deck_id)
    private Set<Long> decks;

    // (user_id) -> (role)
    private Map<Long, UserPodRole> participants;

    // (deck_id)
    private Set<Long> games;
}
