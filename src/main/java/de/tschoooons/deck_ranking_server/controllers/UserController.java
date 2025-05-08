package de.tschoooons.deck_ranking_server.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tschoooons.deck_ranking_server.dtos.RegisterUserDto;
import de.tschoooons.deck_ranking_server.dtos.UserDto;
import de.tschoooons.deck_ranking_server.entities.Deck;
import de.tschoooons.deck_ranking_server.entities.User;
import de.tschoooons.deck_ranking_server.services.Mapper;
import de.tschoooons.deck_ranking_server.services.UserService;
import jakarta.validation.Valid;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.springframework.web.bind.annotation.PutMapping;

@CrossOrigin
@RequestMapping("api/users")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(
        UserService userService
    ) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserDto getId(@PathVariable long id) {
        User user = userService.getByIdLoadLazyFetches(id);
        return Mapper.toDto(user);
    }

    @CrossOrigin
    @GetMapping("")
    public List<UserDto> allUsers() {
        System.out.println("Did we get here?");
        return userService.allUsers().stream()
            .map(Mapper::toDto).toList();
    }
    
    @PostMapping("")
    public UserDto registerUser(@Valid @RequestBody RegisterUserDto registerUserDto) {
        User user = userService.register(registerUserDto);

        return Mapper.toDto(user);
    }

    @PatchMapping("/{id}")
    public UserDto updateUserPartial(@PathVariable long id, @RequestBody RegisterUserDto userDto) {
        User updatedUser = userService.update(id, userDto);
        return Mapper.toDto(updatedUser);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable long id, @Valid @RequestBody RegisterUserDto userDto) {
        if(userDto.getPodRoles() == null){
            userDto.setPodRoles(new HashMap<>());
        }
        if(userDto.getDecks() == null){
            userDto.setDecks(new HashSet<>());
        }
        
        User updatedUser = userService.update(id, userDto);
        return Mapper.toDto(updatedUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.delete(id);
    }

    // ===== DECKS =====
    @GetMapping("/{id}/decks")
    public List<Deck> getUserDecks(@PathVariable long id) {
        return Collections.emptyList();
    }
}
