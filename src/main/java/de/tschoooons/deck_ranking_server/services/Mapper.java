package de.tschoooons.deck_ranking_server.services;

import java.util.ArrayList;
import java.util.List;

import de.tschoooons.deck_ranking_server.dtos.DeckDto;
import de.tschoooons.deck_ranking_server.dtos.GameDto;
import de.tschoooons.deck_ranking_server.dtos.GamePlacementDto;
import de.tschoooons.deck_ranking_server.dtos.MinDeckDto;
import de.tschoooons.deck_ranking_server.dtos.MinGameDto;
import de.tschoooons.deck_ranking_server.dtos.MinUserDto;
import de.tschoooons.deck_ranking_server.dtos.UserDto;
import de.tschoooons.deck_ranking_server.entities.Deck;
import de.tschoooons.deck_ranking_server.entities.Game;
import de.tschoooons.deck_ranking_server.entities.GamePlacement;
import de.tschoooons.deck_ranking_server.entities.User;

public class Mapper {

    public static DeckDto toDto(Deck deck){
        return new DeckDto(
            deck.getId(),
            deck.getName(),
            deck.getCommander(),
            deck.getRating()
        );
    }

    public static MinDeckDto toMinDto(Deck deck) {
        return new MinDeckDto(deck.getId(), deck.getName());
    }

    public static UserDto toDto(User user) {
        List<MinDeckDto> decks = new ArrayList<>();
        
        return new UserDto(
            user.getId(),
            user.getUsername(),
            decks
        );
    }

    public static MinUserDto toMinDto(User user) {
        return new MinUserDto(user.getId(), user.getUsername());
    }

    public static GameDto toDto(Game game) {
        List<GamePlacementDto> placements = new ArrayList<>();
        game.getPlacements().forEach(placement -> placements.add(toDto(placement)));
        
        return new GameDto(
            game.getId(),
            game.getPlayedAt(),
            game.getComment(),
            game.getParticipants(),
            placements
        );
    }

    public static MinGameDto toMinDto(Game game) {
        List<GamePlacementDto> placements = new ArrayList<>();
        game.getPlacements().forEach(placement -> placements.add(toDto(placement)));
        
        return new MinGameDto(
            game.getId(),
            game.getPlayedAt(),
            game.getParticipants(),
            placements
        );
    }

    public static GamePlacementDto toDto(GamePlacement placement) {
        return new GamePlacementDto(
            toMinDto(placement.getDeck()), 
            toMinDto(placement.getPlayer()),
            placement.getPosition(),
            placement.getRating());
    }
}
