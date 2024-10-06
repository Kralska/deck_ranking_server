package de.tschoooons.deck_ranking_server.services;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import de.tschoooons.deck_ranking_server.dtos.RegisterGameDto;
import de.tschoooons.deck_ranking_server.entities.Deck;
import de.tschoooons.deck_ranking_server.entities.Game;
import de.tschoooons.deck_ranking_server.entities.GamePlacement;
import de.tschoooons.deck_ranking_server.entities.Pod;
import de.tschoooons.deck_ranking_server.entities.PodGame;
import de.tschoooons.deck_ranking_server.errors.EntityNotInDBException;
import de.tschoooons.deck_ranking_server.repositories.DeckRepository;
import de.tschoooons.deck_ranking_server.repositories.GameRepository;
import de.tschoooons.deck_ranking_server.repositories.PodRepository;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashSet;

@Service
@Transactional
public class GameService {
    private final GameRepository gameRepository;
    private final DeckRepository deckRepository;
    private final PodRepository podRepository;
    private final PodCalculationService podCalculationService;

    public GameService(
        GameRepository gameRepository, 
        DeckRepository deckRepository,
        PodRepository podRepository,
        PodCalculationService podCalculationService
    ) {
        this.gameRepository = gameRepository;
        this.deckRepository = deckRepository;
        this.podRepository = podRepository;
        this.podCalculationService = podCalculationService;
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

        if (gameDto.getPlacements() != null) {
            HashSet<GamePlacement> placements = new HashSet<GamePlacement>();
            for(Map.Entry<Integer, Integer> placement: gameDto.getPlacements().entrySet()) {
                Deck deck = deckRepository.findById(Long.valueOf(placement.getKey())).get();
                placements.add(new GamePlacement(game, deck, placement.getValue()));
            }
            game.setPlacements(new ArrayList<GamePlacement>(placements));
        }

        HashSet<PodGame> pods = new HashSet<PodGame>();
        if (gameDto.getPods() != null) {
            for(Long pod_id : gameDto.getPods()) {
                Pod pod = podRepository.findById(pod_id).get();
                pods.add(new PodGame(pod, game));
            }
            game.setPodGames(new ArrayList<PodGame>(pods));
        } 

        Game savedGame = gameRepository.save(game);

        // Update ratings in all pods!
        for(PodGame podGame : pods){
            podCalculationService.CalculateRatings(podGame.getPod(), 1000);
            podRepository.save(podGame.getPod());
        }

        return savedGame;
    }
}
