package de.tschoooons.deck_ranking_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GamePlacementDto {
    
    private MinDeckDto deck;

    private int position;

}
