package de.tschoooons.deck_ranking_server.dtos;

import java.util.Map;
import java.util.Set;

import de.tschoooons.deck_ranking_server.entities.UserPodRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterUserDto {
    @NotBlank
    @NotNull
    @Size(max = 500)
    private String username;

    private Set<Long> decks;

    private Map<Long, UserPodRole> podRoles;
}
