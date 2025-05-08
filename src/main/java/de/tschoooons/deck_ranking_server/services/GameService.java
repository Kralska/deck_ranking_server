package de.tschoooons.deck_ranking_server.services;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import de.tschoooons.deck_ranking_server.dtos.GameDto;
import de.tschoooons.deck_ranking_server.dtos.GamePlacementDto;
import de.tschoooons.deck_ranking_server.entities.Game;
import de.tschoooons.deck_ranking_server.entities.GamePlacement;
import de.tschoooons.deck_ranking_server.repositories.DeckRepository;
import de.tschoooons.deck_ranking_server.repositories.GameRepository;
import de.tschoooons.deck_ranking_server.repositories.UserRepository;
import de.tschoooons.deck_ranking_server.errors.EntityNotInDBException;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class GameService {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final DeckRepository deckRepository;

    public GameService(
        GameRepository gameRepository, 
        DeckRepository deckRepository,
        UserRepository userRepository
    ) {
        this.gameRepository = gameRepository;
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
    }

    public ArrayList<Game> allGames() {
        ArrayList<Game> games = new ArrayList<Game>();
        gameRepository.findAll().forEach(games::add);
        for(Game game : games) {
            game.getPlacements().size();
        }
        return games;
    }

    public Game getById(long id){
        return gameRepository.findById(id)
            .orElseThrow(() -> new EntityNotInDBException("No game with id " + id + " found."));
    }

    public Game getByIdLoadLazyFetches(long id){
        Game game = getById(id);
        Hibernate.initialize(game.getPlacements());
        Hibernate.initialize(game.getPodGames());
        return game;
    }

    @Transactional
    public Game register(GameDto gameDto) {
        Game game = new Game();
        game.setPlayedAt(gameDto.getPlayedAt());
        game.setComment(gameDto.getComment());
        game.setParticipants(gameDto.getParticipants());
        setPlacementsFromDto(game, gameDto);

        return gameRepository.save(game);
    }

    public Game update(long id, GameDto gameDto) {
        Game game = getById(id);
        gameDto.setComment(gameDto.getComment());
        gameDto.setParticipants(gameDto.getParticipants());
        gameDto.setPlayedAt(gameDto.getPlayedAt());

        setPlacementsFromDto(game, gameDto);

        return gameRepository.save(game);
    }

    public void delete(long id) {
        gameRepository.deleteById(id);
    }

    private void setPlacementsFromDto(Game game, GameDto dto) {
        if (dto.getPlacements() == null) {
            game.getPlacements().clear();
            return;
        }

        List<GamePlacement> oldPlacements = game.getPlacements();
        List<GamePlacement> newPlacements = new ArrayList<>();
        for(GamePlacementDto entry : dto.getPlacements()) {
            GamePlacement gamePlacement = new GamePlacement(
                game,
                deckRepository.findById(entry.getDeck().getId()).orElseThrow(),
                userRepository.findById(entry.getPlayer().getId()).orElseThrow(),
                entry.getPosition());

            int idx = oldPlacements.indexOf(gamePlacement);
            if(idx != -1) {
                oldPlacements.get(idx).setPosition(entry.getPosition());
                newPlacements.add(oldPlacements.get(idx));
            } else {
                newPlacements.add(gamePlacement);
            }
        }
        game.getPlacements().clear();
        game.getPlacements().addAll(newPlacements);
    }
}
