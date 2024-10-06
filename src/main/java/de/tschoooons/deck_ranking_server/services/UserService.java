package de.tschoooons.deck_ranking_server.services;

import org.hibernate.Hibernate;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import de.tschoooons.deck_ranking_server.dtos.RegisterUserDto;
import de.tschoooons.deck_ranking_server.entities.User;
import de.tschoooons.deck_ranking_server.errors.EntityNotInDBException;
import de.tschoooons.deck_ranking_server.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    
    private static final ExampleMatcher MATCH_ANY = ExampleMatcher
        .matchingAny()
        .withIgnorePaths("id")
        .withIgnoreCase()
        .withMatcher("username", matcher -> matcher.exact());

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
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

    public User register(RegisterUserDto registerUserDto) {
        User newUser = new User();
        newUser.setUsername(registerUserDto.getUsername());

        return userRepository.save(newUser);
    }
}
