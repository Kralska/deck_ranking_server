package de.tschoooons.deck_ranking_server.dtos;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MinGameDto {

    private long id;

    private Date playedAt;

    private int participants;

    private List<GamePlacementDto> placements;

}
