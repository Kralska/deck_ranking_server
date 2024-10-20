package de.tschoooons.deck_ranking_server.dtos;

import java.util.Map;
import java.util.Set;

import org.hibernate.validator.constraints.UniqueElements;

import de.tschoooons.deck_ranking_server.entities.UserPodRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterPodDto {
    @NotBlank
    @NotNull
    @Size(max = 500)
    private String name;
    
    // Decks belonging to the pod
    // (deck_id)
    @UniqueElements
    private Set<Long> decks;

    // (user_id) -> (role)
    @NotEmpty
    @UniqueElements
    private Map<Long, UserPodRole> participants;

    // (deck_id)
    @UniqueElements
    private Set<Long> games;
}
