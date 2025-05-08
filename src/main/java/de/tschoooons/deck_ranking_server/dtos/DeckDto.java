package de.tschoooons.deck_ranking_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeckDto {

    private Long id;

    private String name;

    private String commander;

    private int rating = 1000;

}
