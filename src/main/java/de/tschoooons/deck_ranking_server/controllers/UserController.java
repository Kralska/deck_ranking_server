package de.tschoooons.deck_ranking_server.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tschoooons.deck_ranking_server.dtos.RegisterUserDto;
import de.tschoooons.deck_ranking_server.entities.User;
import de.tschoooons.deck_ranking_server.services.UserService;

import java.util.List;


@RequestMapping("api/users")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getId(@PathVariable long id) {
        User user = userService.getByIdLoadLazyFetches(id);
        return user;
    }

    @GetMapping(value={"", "/"})
    public List<User> allUsers() {
        return userService.allUsers();
    }
    
    @PostMapping(value = {"", "/", "/register"})
    public User registerUser(@RequestBody RegisterUserDto registerUserDto) {
        User user = userService.register(registerUserDto);

        return user;
    }
}
