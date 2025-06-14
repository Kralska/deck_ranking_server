package de.tschoooons.deck_ranking_server.services;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import de.tschoooons.deck_ranking_server.dtos.GameDto;
import de.tschoooons.deck_ranking_server.dtos.GamePlacementDto;
import de.tschoooons.deck_ranking_server.entities.Deck;
import de.tschoooons.deck_ranking_server.entities.Game;
import de.tschoooons.deck_ranking_server.entities.GamePlacement;
import de.tschoooons.deck_ranking_server.repositories.DeckRepository;
import de.tschoooons.deck_ranking_server.repositories.GameRepository;
import de.tschoooons.deck_ranking_server.repositories.UserRepository;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GameService {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final DeckRepository deckRepository;
    private final PodCalculationService calculationService;

    public GameService(
        GameRepository gameRepository, 
        DeckRepository deckRepository,
        UserRepository userRepository,
        PodCalculationService calculationService
    ) {
        this.gameRepository = gameRepository;
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
        this.calculationService = calculationService;
    }

    /**
     * 
     * @return
     */
    public List<Game> allGames() {
        ArrayList<Game> games = new ArrayList<Game>();
        gameRepository.findAllWithPlacements().forEach(games::add);
        return games;
    }

    public Optional<Game> getById(long id){
        return getById(id, false);
    }

    /**
     * Fetches a {@link Game} from the database by its {@code id}.
     * @param id Id of the game
     * @param loadAllInformation should additional game information be loaded? (accesses other 
     *        database tables)
     * @return {@link Game}, <strong>IF</strong> a game with {@code id} was found in the database
     *    <br> {@link Optional#empty()}, <strong>OTHERWISE</strong>
     */
    public Optional<Game> getById(long id, boolean loadAllInformation){
        Optional<Game> game = gameRepository.findById(id);
        if(!loadAllInformation){
            return game;
        }
        if(game.isEmpty()){
            return game;
        }
        Hibernate.initialize(game.get().getPlacements());
        Hibernate.initialize(game.get().getPodGames());
        return game;
    }

    /**
     * Adds a new game to the database
     * @param gameDto data for the new game
     * @return the game, as it was registered in the database
     */
    @Transactional
    public Game register(GameDto gameDto) {
        Game game = new Game();
        game.setPlayedAt(gameDto.getPlayedAt());
        game.setComment(gameDto.getComment());
        game.setParticipants(gameDto.getParticipants());
        setPlacementsFromDto(game, gameDto);

        game = gameRepository.save(game);

        calculationService.calculateRatings();

        return game;
    }

    public Game update(long id, GameDto gameDto) {
        Optional<Game> optGame = getById(id);
        if(optGame.isEmpty()){
            return null;
        }
        Game game = optGame.get();
        gameDto.setComment(gameDto.getComment());
        gameDto.setParticipants(gameDto.getParticipants());
        gameDto.setPlayedAt(gameDto.getPlayedAt());

        setPlacementsFromDto(game, gameDto);

        return gameRepository.save(game);
    }

    public void delete(long id) {
        gameRepository.deleteById(id);
    }

    /**
     * Returns all games that {@code deck} participated in.
     * @param deck Deck
     * @return List of games.
     * @see GameService#getGamesFrom(Iterable)
     */
    public List<Game> getGamesFrom(Deck deck){
        List<Deck> deckList = new ArrayList<>(1);
        deckList.add(deck);
        return getGamesFrom(deckList);
    }

    public List<Game> getGamesFrom(Iterable<Deck> decks){
        Iterable<Game> iter = gameRepository.findAllByDecks(decks);
        List<Game> games = new ArrayList<>();
        iter.forEach(games::add);
        return games;
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
