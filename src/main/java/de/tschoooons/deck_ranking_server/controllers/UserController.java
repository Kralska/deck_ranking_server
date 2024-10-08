package de.tschoooons.deck_ranking_server.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tschoooons.deck_ranking_server.dtos.RegisterUserDto;
import de.tschoooons.deck_ranking_server.entities.Deck;
import de.tschoooons.deck_ranking_server.entities.User;
import de.tschoooons.deck_ranking_server.services.DeckService;
import de.tschoooons.deck_ranking_server.services.UserService;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.springframework.web.bind.annotation.PutMapping;




@RequestMapping("api/users")
@RestController
public class UserController {
    private final UserService userService;
    private final DeckService deckService;

    public UserController(
        UserService userService,
        DeckService deckService
    ) {
        this.userService = userService;
        this.deckService = deckService;
    }

    @GetMapping("/{id}")
    public User getId(@PathVariable long id) {
        User user = userService.getByIdLoadLazyFetches(id);
        return user;
    }

    @GetMapping("")
    public List<User> allUsers() {
        return userService.allUsers();
    }
    
    @PostMapping("")
    public User registerUser(@Valid @RequestBody RegisterUserDto registerUserDto) {
        User user = userService.register(registerUserDto);

        return user;
    }

    @PatchMapping("/{id}")
    public User updateUserPartial(@PathVariable long id, @RequestBody RegisterUserDto userDto) {
        User updatedUser = userService.update(id, userDto);
        return updatedUser;
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable long id, @Valid @RequestBody RegisterUserDto userDto) {
        if(userDto.getPodRoles() == null){
            userDto.setPodRoles(new HashMap<>());
        }
        if(userDto.getDecks() == null){
            userDto.setDecks(new HashSet<>());
        }
        
        User updatedUser = userService.update(id, userDto);
        
        return updatedUser;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.delete(id);
    }

    // ===== DECKS =====
    @GetMapping("/{id}/decks")
    public List<Deck> getUserDecks(@PathVariable long id) {
        return deckService.getAllDecksOwnedBy(id);
    }
}
