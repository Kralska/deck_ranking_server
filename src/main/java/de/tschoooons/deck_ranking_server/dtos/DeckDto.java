package de.tschoooons.deck_ranking_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeckDto {

    private long id;

    private String name;

    private MinUserDto owner;

    private int rating;

}
