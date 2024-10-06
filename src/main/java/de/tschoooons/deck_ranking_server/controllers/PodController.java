package de.tschoooons.deck_ranking_server.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tschoooons.deck_ranking_server.dtos.RegisterPodDto;
import de.tschoooons.deck_ranking_server.entities.Pod;
import de.tschoooons.deck_ranking_server.services.PodService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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
    
}
