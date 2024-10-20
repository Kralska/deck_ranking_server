package de.tschoooons.deck_ranking_server.services;

import org.hibernate.Hibernate;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Optional;
import de.tschoooons.deck_ranking_server.dtos.RegisterUserDto;
import de.tschoooons.deck_ranking_server.entities.Deck;
import de.tschoooons.deck_ranking_server.entities.PodParticipant;
import de.tschoooons.deck_ranking_server.entities.User;
import de.tschoooons.deck_ranking_server.entities.UserPodRole;
import de.tschoooons.deck_ranking_server.errors.EntityNotInDBException;
import de.tschoooons.deck_ranking_server.repositories.DeckRepository;
import de.tschoooons.deck_ranking_server.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final DeckRepository deckRepository;
    private final PodService podService;
    
    private static final ExampleMatcher MATCH_ANY = ExampleMatcher
        .matchingAny()
        .withIgnorePaths("id")
        .withIgnoreCase()
        .withMatcher("username", matcher -> matcher.exact());

    public UserService(
        UserRepository userRepository,
        DeckRepository deckRepository,
        PodService podService
    ){
        this.userRepository = userRepository;
        this.deckRepository = deckRepository;
        this.podService = podService;
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public User getById(Long id) 
    throws EntityNotInDBException{
        return userRepository.findById(id)
            .orElseThrow(() -> new EntityNotInDBException("No user with id " + id + " found."));
    }

    public User getByIdLoadLazyFetches(long id) {
        User user = getById(id);
        Hibernate.initialize(user.getPodRoles());
        Hibernate.initialize(user.getDecks());
        return user;
    }

    public User getUserByName(String username) 
    throws EntityNotFoundException{
        // Create Database query
        User searchedUser = new User();
        searchedUser.setUsername(username);
        Example<User> ex = Example.of(searchedUser, MATCH_ANY);

        // Run database query
        Optional<User> user = userRepository.findOne(ex);
        if(user.isPresent()) {
            return user.get();
        }
        throw new EntityNotFoundException("User could not be found");
    }

    public void delete(long id) {
        userRepository.deleteById(id);
    }

    public User register(RegisterUserDto userDto) {
        User newUser = new User();
        newUser.setUsername(userDto.getUsername());
        setUserDecksFromDto(newUser, userDto);
        setUserRolesFromDto(newUser, userDto);

        return userRepository.save(newUser);
    }

    /*
     * Updates the user with the id {id}.
     * 
     * Fields of userDto that are null are ignored(will not be updated)
     */
    public User update(long id, RegisterUserDto userDto) {
        User user = getById(id);
        if(userDto.getUsername() != null) {
            user.setUsername(userDto.getUsername());
        }
        if(userDto.getDecks() != null) {
            setUserDecksFromDto(user, userDto);
        }
        if(userDto.getPodRoles() != null) {
            setUserRolesFromDto(user, userDto);
        }
        return userRepository.save(user);
    }

    private void setUserDecksFromDto(User user, RegisterUserDto dto) {
        user.getDecks().clear();
        if(dto.getDecks() == null) {
            return;
        }
        Iterable<Deck> decks = deckRepository.findAllById(dto.getDecks());
        for(Deck deck : decks) {
            user.getDecks().add(deck);
        }
    }

    private void setUserRolesFromDto(User user, RegisterUserDto dto) {
        if(dto.getPodRoles() == null){
            user.getPodRoles().clear();
            return;
        }

        List<PodParticipant> oldParticipations = user.getPodRoles();
        List<PodParticipant> newParticipations = new ArrayList<>();
        for(Map.Entry<Long, UserPodRole> entry : dto.getPodRoles().entrySet()) {
            PodParticipant podParticipant = new PodParticipant(podService.getById(entry.getKey()), user, entry.getValue());
            int idx = oldParticipations.indexOf(podParticipant);
            if(idx != -1){
                oldParticipations.get(idx).setRole(entry.getValue());
                newParticipations.add(oldParticipations.get(idx));
            } else {
                newParticipations.add(podParticipant);
            }
        }
        user.getPodRoles().clear();
        user.getPodRoles().addAll(newParticipations);
    }
}
