package de.tschoooons.deck_ranking_server.services;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.tschoooons.deck_ranking_server.dtos.RegisterPodDto;
import de.tschoooons.deck_ranking_server.entities.Deck;
import de.tschoooons.deck_ranking_server.entities.Game;
import de.tschoooons.deck_ranking_server.entities.Pod;
import de.tschoooons.deck_ranking_server.entities.User;
import de.tschoooons.deck_ranking_server.entities.UserPodRole;
import de.tschoooons.deck_ranking_server.repositories.DeckRepository;
import de.tschoooons.deck_ranking_server.repositories.GameRepository;
import de.tschoooons.deck_ranking_server.repositories.PodRepository;
import de.tschoooons.deck_ranking_server.repositories.UserRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class PodService {
    private final PodRepository podRepository;
    private final DeckRepository deckRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final PodCalculationService podCalculationService;
    

    public PodService(
        PodRepository podRepository,
        DeckRepository deckRepository,
        GameRepository gameRepository,
        UserRepository userRepository,
        PodCalculationService podCalculationService
    ) {
        this.podRepository = podRepository;
        this.deckRepository = deckRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.podCalculationService = podCalculationService;
    }

    public Pod getById(long id) {
        return podRepository.findById(id).get();
    }

    public Pod getByIdLoadLazyFetches(long id) {
        Pod pod = getById(id);
        Hibernate.initialize(pod.getPodParticipants());
        Hibernate.initialize(pod.getDeckRatings());
        Hibernate.initialize(pod.getPodGames());
        return pod;
    }

    public List<Pod> allPods() {
        List<Pod> pods = new ArrayList<Pod>();
        podRepository.findAll().forEach(pods::add);
        return pods;
    }

    public Pod register(RegisterPodDto podDto) {
        Pod pod = new Pod();
        pod.setName(podDto.getName());
        
        if(podDto.getDecks() != null) {
            List<Deck> decks = new ArrayList<Deck>();
            for (Long deck_id : podDto.getDecks()) {
                Deck deck = deckRepository.findById(deck_id).get();
                decks.add(deck);
            }
            pod.setDecks(decks);
        }
        
        if(podDto.getGames() != null) {
            List<Game> games = new ArrayList<Game>();
            for(int game_id : podDto.getGames()) {
                Game game = gameRepository.findById((long) game_id).get();
                games.add(game);
            }
            pod.setGames(games);
        }
        
        HashMap<User, UserPodRole> users = new HashMap<User, UserPodRole>();
        for (Map.Entry<Integer, UserPodRole> entry : podDto.getParticipants().entrySet()) {
            User user = userRepository.findById((long) entry.getKey()).get();
            users.put(user, entry.getValue());
        }
        pod.setUsers(users);

        podCalculationService.CalculateRatings(pod, 1000);

        pod = podRepository.save(pod);

        return pod;
    }
}
