package de.tschoooons.deck_ranking_server.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tschoooons.deck_ranking_server.dtos.RegisterPodDto;
import de.tschoooons.deck_ranking_server.entities.Pod;
import de.tschoooons.deck_ranking_server.services.PodService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



@RequestMapping("api/pods")
@RestController
public class PodController {
    
    private PodService podService;

    PodController(PodService podService) {
        this.podService = podService;
    }

    @GetMapping("/{id}")
    public Pod getPod(@PathVariable long id) {
        return podService.getByIdLoadLazyFetches(id);
    }
    
    @GetMapping(value = {"", "/"})
    public List<Pod> allPods() {  
        return podService.allPods();
    }

    @PostMapping(value= {"", "/", "register"})
    public Pod register(@RequestBody RegisterPodDto podDto) {
        return podService.register(podDto);
    }
 
    @PutMapping("/{id}")
    public Pod updatePod(@PathVariable long id, @RequestBody RegisterPodDto updatedPod) {
        // Put in an empty map for participants instead of null to ensure removal
        if(updatedPod.getParticipants() == null) {
            updatedPod.setParticipants(new HashMap<>());
        }
        // Put in an empty set for decks instead of null to ensure removal
        if(updatedPod.getDecks() == null) {
            updatedPod.setDecks(new HashSet<>());
        }
        // Put in an empty set for gmaes instead of null to ensure removal
        if(updatedPod.getGames() == null) {
            updatedPod.setGames(new HashSet<>());
        }
        return podService.update(id, updatedPod);
    }

    @PatchMapping("/{id}")
    public Pod updatePodPartial(@PathVariable long id, @RequestBody RegisterPodDto updatedPod) {
        return podService.update(id, updatedPod);
    }
}
