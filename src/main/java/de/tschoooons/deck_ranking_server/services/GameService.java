package de.tschoooons.deck_ranking_server.services;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import de.tschoooons.deck_ranking_server.dtos.RegisterGameDto;
import de.tschoooons.deck_ranking_server.entities.Game;
import de.tschoooons.deck_ranking_server.entities.GamePlacement;
import de.tschoooons.deck_ranking_server.entities.Pod;
import de.tschoooons.deck_ranking_server.entities.PodGame;
import de.tschoooons.deck_ranking_server.repositories.DeckRepository;
import de.tschoooons.deck_ranking_server.repositories.GameRepository;
import de.tschoooons.deck_ranking_server.repositories.PodRepository;
import de.tschoooons.deck_ranking_server.errors.EntityNotInDBException;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional
public class GameService {
    private final GameRepository gameRepository;
    private final DeckRepository deckRepository;
    private final PodRepository podRepository;

    public GameService(
        GameRepository gameRepository, 
        DeckRepository deckRepository,
        PodRepository podRepository,
        PodCalculationService podCalculationService
    ) {
        this.gameRepository = gameRepository;
        this.deckRepository = deckRepository;
        this.podRepository = podRepository;
    }

    public ArrayList<Game> allGames() {
        ArrayList<Game> games = new ArrayList<Game>();
        gameRepository.findAll().forEach(games::add);
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



    public Game register(RegisterGameDto gameDto) {
        Game game = new Game();
        game.setPlayedAt(gameDto.getPlayedAt());
        game.setComment(gameDto.getComment());
        game.setParticipants(gameDto.getParticipants());

        setPlacementsFromDto(game, gameDto);

        HashSet<PodGame> pods = new HashSet<PodGame>();
        if (gameDto.getPods() != null) {
            for(Long pod_id : gameDto.getPods()) {
                Pod pod = podRepository.findById(pod_id).get();
                pods.add(new PodGame(pod, game));
            }
            game.setPodGames(new ArrayList<PodGame>(pods));
        } 

        Game savedGame = gameRepository.save(game);

        return savedGame;
    }

    public Game update(long id, RegisterGameDto gameDto) {
        Game game = getById(id);
        gameDto.setComment(gameDto.getComment());
        gameDto.setParticipants(gameDto.getParticipants());
        gameDto.setPlayedAt(gameDto.getPlayedAt());

        setPlacementsFromDto(game, gameDto);
        setPodsFromDto(game, gameDto);

        return gameRepository.save(game);
    }

    public void delete(long id) {
        gameRepository.deleteById(id);
    }

    private void setPlacementsFromDto(Game game, RegisterGameDto dto) {
        if (dto.getPlacements() == null) {
            game.getPlacements().clear();
            return;
        }

        List<GamePlacement> oldPlacements = game.getPlacements();
        List<GamePlacement> newPlacements = new ArrayList<>();
        for(Map.Entry<Long, Integer> entry : dto.getPlacements().entrySet()) {
            GamePlacement gamePlacement = new GamePlacement(game, deckRepository.findById(entry.getKey()).get(), entry.getValue());
            int idx = oldPlacements.indexOf(gamePlacement);
            if(idx != -1) {
                oldPlacements.get(idx).setPosition(entry.getValue());
                newPlacements.add(oldPlacements.get(idx));
            } else {
                newPlacements.add(gamePlacement);
            }
        }
        game.getPlacements().clear();
        game.getPlacements().addAll(newPlacements);
    }

    private void setPodsFromDto(Game game, RegisterGameDto dto) {
        if(dto.getPods() == null) {
            game.getPodGames().clear();
            return;
        }

        List<PodGame> oldPodGames = game.getPodGames();
        List<PodGame> newPodGames = new ArrayList<>();
        for(Long podId : dto.getPods()){
            Pod pod = podRepository.findById(podId).get();
            PodGame podGame = new PodGame(pod, game);
            int idx = oldPodGames.indexOf(podGame);
            if(idx != -1) {
                newPodGames.add(oldPodGames.get(idx));
            } else {
                newPodGames.add(podGame);
            }
        }
        game.getPodGames().clear();
        game.getPodGames().addAll(newPodGames);
    }
}
