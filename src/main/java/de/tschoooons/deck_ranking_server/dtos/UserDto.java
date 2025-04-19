package de.tschoooons.deck_ranking_server.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {

    private long id;

    private String username;

    private List<MinDeckDto> decks;

}
