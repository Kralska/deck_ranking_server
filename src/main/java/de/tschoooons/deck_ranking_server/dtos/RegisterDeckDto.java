package de.tschoooons.deck_ranking_server.dtos;

import java.util.Map;
import java.util.Set;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterDeckDto {
    @NotBlank
    @NotNull
    @Size(max = 500)
    private String name;

    private String commander;

    @NotNull
    @Min(value = 0)
    private Long owner;

    // GameID -> position
    private Map<Long, Integer> placements = null;

    // PodId
    private Set<Long> pods = null;
}
