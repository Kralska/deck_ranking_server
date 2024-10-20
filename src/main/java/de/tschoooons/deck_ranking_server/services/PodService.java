package de.tschoooons.deck_ranking_server.services;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import de.tschoooons.deck_ranking_server.dtos.RegisterPodDto;
import de.tschoooons.deck_ranking_server.entities.Deck;
import de.tschoooons.deck_ranking_server.entities.DeckRating;
import de.tschoooons.deck_ranking_server.entities.Game;
import de.tschoooons.deck_ranking_server.entities.Pod;
import de.tschoooons.deck_ranking_server.entities.PodGame;
import de.tschoooons.deck_ranking_server.entities.PodParticipant;
import de.tschoooons.deck_ranking_server.entities.User;
import de.tschoooons.deck_ranking_server.entities.UserPodRole;
import de.tschoooons.deck_ranking_server.errors.EntityNotInDBException;
import de.tschoooons.deck_ranking_server.repositories.PodRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class PodService {
    private final PodRepository podRepository;
    private final DeckService deckService;
    private final GameService gameService;
    private final UserService userService;
    private final PodCalculationService podCalculationService;
    

    public PodService(
        PodRepository podRepository,
        DeckService deckService,
        GameService gameService,
        UserService userService,
        PodCalculationService podCalculationService
    ) {
        this.podRepository = podRepository;
        this.deckService = deckService;
        this.gameService = gameService;
        this.userService = userService;
        this.podCalculationService = podCalculationService;
    }

    public Pod getById(long id) {
        return podRepository.findById(id)
         .orElseThrow(() -> new EntityNotInDBException("No pod with id " + id + " found."));
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
        
        setDecksFromDto(pod, podDto);
        setGamesFromDto(pod, podDto);
        setUsersFromDto(pod, podDto);

        podCalculationService.CalculateRatings(pod, 1000);
        pod = podRepository.save(pod);

        return pod;
    }

    /*
     * Updates the pod with id {id}.
     * 
     * Fields of dto that are null are ignored and will not be updated.
     */
    public Pod update(long id, RegisterPodDto dto) {
        Pod pod = getById(id);
        if(dto.getName() != null) {
            pod.setName(dto.getName());
        }
        if(dto.getDecks() != null) {
            setDecksFromDto(pod, dto);
        }
        if(dto.getGames() != null) {
            setGamesFromDto(pod, dto);
        }
        if(dto.getParticipants() != null) {
            setUsersFromDto(pod, dto);
        }

        return podRepository.save(pod);
    }

    private void setDecksFromDto(Pod pod, RegisterPodDto dto) {
        if(dto.getDecks() == null) {
            pod.getDeckRatings().clear();
            return;
        }
        
        List<DeckRating> oldDeckRatings = pod.getDeckRatings();
        List<DeckRating> newDeckRatings = new ArrayList<>();

        for(long deckId : dto.getDecks()) {
            Deck deck = deckService.getById(deckId);
            DeckRating deckRating = new DeckRating(pod, deck);
            int idx = oldDeckRatings.indexOf(deckRating);
            if(idx != -1) {
                newDeckRatings.add(oldDeckRatings.get(idx));
            } else {
                newDeckRatings.add(deckRating);
            }
        }
        pod.getDeckRatings().clear();
        pod.getDeckRatings().addAll(newDeckRatings);
    }

    private void setGamesFromDto(Pod pod, RegisterPodDto dto) {
        if(dto.getGames() == null) {
            pod.getPodGames().clear();
            return;
        }

        List<PodGame> oldPodGames = pod.getPodGames();
        List<PodGame> newPodGames = new ArrayList<>();

        for(long gameId : dto.getGames()) {
            Game game = gameService.getById(gameId);
            PodGame podGame = new PodGame(pod, game);
            int idx = oldPodGames.indexOf(podGame);
            if(idx != -1) {
                newPodGames.add(oldPodGames.get(idx));
            } else {
                newPodGames.add(podGame);
            }
        }
        pod.getPodGames().clear();
        pod.getPodGames().addAll(newPodGames);
    }

    private void setUsersFromDto(Pod pod, RegisterPodDto dto) {
        if(dto.getParticipants() == null){
            pod.getPodParticipants().clear();
            return;
        }

        List<PodParticipant> oldParticipants = pod.getPodParticipants();
        List<PodParticipant> newParticipants = new ArrayList<>();
        for(Map.Entry<Long, UserPodRole> entry : dto.getParticipants().entrySet()) {
            User user = userService.getById(entry.getKey());
            PodParticipant podParticipant = new PodParticipant(pod, user, entry.getValue());
            int idx = oldParticipants.indexOf(podParticipant);
            if (idx != -1) {
                newParticipants.add(oldParticipants.get(idx));
            } else {
                newParticipants.add(podParticipant);
            }
        }
        pod.getPodParticipants().clear();
        pod.getPodParticipants().addAll(newParticipants);
    }
}
