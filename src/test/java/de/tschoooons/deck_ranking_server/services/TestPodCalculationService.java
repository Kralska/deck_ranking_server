package de.tschoooons.deck_ranking_server.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import de.tschoooons.deck_ranking_server.entities.Deck;
import de.tschoooons.deck_ranking_server.entities.Game;
import de.tschoooons.deck_ranking_server.entities.GamePlacement;
import de.tschoooons.deck_ranking_server.entities.Pod;

public class TestPodCalculationService {

    private EloService eloService = new EloService();

    @InjectMocks
    private PodCalculationService podCalculationService = new PodCalculationService(eloService);

    @Test
    public void DeckPerformanceInGame() {
        Deck deck1 = new Deck();
        deck1.setId(1); 
        deck1.setName("deck1");
        Deck deck2 = new Deck();
        deck2.setId(2);
        deck2.setName("deck2");
        Deck deck3 = new Deck();
        deck3.setId(3);
        deck3.setName("deck3");
        Deck deck4 = new Deck();
        deck4.setId(4);
        deck4.setName("deck4");

        Game game = new Game();
        game.setId(0);
        game.setParticipants(4);
        
        GamePlacement gamePlacement1 = new GamePlacement();
        gamePlacement1.setDeck(deck1);
        gamePlacement1.setGame(game);
        gamePlacement1.setPosition(1);
        
        GamePlacement gamePlacement2 = new GamePlacement();
        gamePlacement2.setDeck(deck2);
        gamePlacement2.setGame(game);
        gamePlacement2.setPosition(2);

        GamePlacement gamePlacement3 = new GamePlacement();
        gamePlacement3.setDeck(deck3);
        gamePlacement3.setGame(game);
        gamePlacement3.setPosition(3);

        GamePlacement gamePlacement4 = new GamePlacement();
        gamePlacement4.setDeck(deck4);
        gamePlacement4.setGame(game);
        gamePlacement4.setPosition(4);

        game.setPlacements(List.of(gamePlacement1, gamePlacement2, gamePlacement3, gamePlacement4));

        // 1 -> 1st(3) | 2 -> 2nd(2) | 3 -> 3rd(1) | 4 -> 4th(0)
        Map<Deck, Float> performances = podCalculationService.CalculatePerformances(game);
        assertEquals(3.0f, performances.get(deck1));
        assertEquals(2.0f, performances.get(deck2));
        assertEquals(1.0f, performances.get(deck3));
        assertEquals(0.0f, performances.get(deck4));

        // 1,2 -> 1st(2.5) | 3 -> 3rd(1) | 4 -> 4th(0)
        gamePlacement2.setPosition(1);
        game.setPlacements(List.of(gamePlacement1, gamePlacement2, gamePlacement3, gamePlacement4));
        performances = podCalculationService.CalculatePerformances(game);
        assertEquals(2.5f, performances.get(deck1));
        assertEquals(2.5f, performances.get(deck2));
        assertEquals(1.0f, performances.get(deck3));
        assertEquals(0.0f, performances.get(deck4));
    }

    @Test
    public void CalculateRatingsForPod() {
        Pod pod = new Pod();
        pod.setDeckRatings(new ArrayList<>());
        
        Deck inklingTokens = new Deck();
        inklingTokens.setName("inklingTokens");
        pod.addDeck(inklingTokens);
        Deck satoruGyruda = new Deck();
        satoruGyruda.setName("satoruGyruda");
        pod.addDeck(satoruGyruda);
        Deck chichiroEquip = new Deck();
        chichiroEquip.setName("chichiroEquip");
        pod.addDeck(chichiroEquip);
        Deck felixFiveBoots = new Deck();
        felixFiveBoots.setName("felixFiveBoots");
        pod.addDeck(felixFiveBoots);
        Deck dinoTribal = new Deck();
        dinoTribal.setName("dinoTribal");
        pod.addDeck(dinoTribal);
        Deck samwiseLifegain = new Deck();
        samwiseLifegain.setName("samwiseLifegain");
        pod.addDeck(samwiseLifegain);
        Deck roxanneAirstrike = new Deck();
        roxanneAirstrike.setName("roxanneAirstrike");
        pod.addDeck(roxanneAirstrike);
        Deck morskaClues = new Deck();
        morskaClues.setName("morskaClues");
        pod.addDeck(morskaClues);
        Deck gontiTheft = new Deck();
        gontiTheft.setName("gontiTheft");
        pod.addDeck(gontiTheft);
        Deck nivMizzet = new Deck();
        nivMizzet.setName("nivMizzet");
        pod.addDeck(nivMizzet);

        Game game1 = new Game();
        game1.setId(1);
        game1.setParticipants(5);
        if(game1.getPlacements() == null) {
            game1.setPlacements(new ArrayList<GamePlacement>());
        }
        game1.addDeck(inklingTokens, 2);
        game1.addDeck(satoruGyruda, 2);
        game1.addDeck(chichiroEquip, 1);
        game1.addDeck(felixFiveBoots, 2);
        game1.addDeck(dinoTribal, 2);
        pod.addGame(game1);

        podCalculationService.CalculateRatingsForPod(pod, 1000);

        assertEquals(1080, pod.getRatingForDeck(chichiroEquip));
        assertEquals(980, pod.getRatingForDeck(inklingTokens));
        assertEquals(1000, pod.getRatingForDeck(samwiseLifegain));
        assertEquals(980, pod.getRatingForDeck(satoruGyruda));
        assertEquals(980, pod.getRatingForDeck(felixFiveBoots));

        Game game2 = new Game();
        game2.setId(2);
        game2.setParticipants(5);
        if(game2.getPlacements() == null) {
            game2.setPlacements(new ArrayList<GamePlacement>());
        }
        game2.addDeck(samwiseLifegain, 1);
        game2.addDeck(roxanneAirstrike, 5);
        game2.addDeck(chichiroEquip, 2);
        game2.addDeck(morskaClues, 2);
        game2.addDeck(gontiTheft, 4);
        pod.addGame(game2);

        podCalculationService.CalculateRatingsForPod(pod, 1000);
        assertEquals(961, pod.getRatingForDeck(gontiTheft));
        assertEquals(921, pod.getRatingForDeck(roxanneAirstrike));
        assertEquals(1021, pod.getRatingForDeck(morskaClues));
        assertEquals(980, pod.getRatingForDeck(inklingTokens));
        assertEquals(1081, pod.getRatingForDeck(samwiseLifegain));
        assertEquals(980, pod.getRatingForDeck(satoruGyruda));
        assertEquals(980, pod.getRatingForDeck(felixFiveBoots));
        assertEquals(980, pod.getRatingForDeck(dinoTribal));

        Game game3 = new Game();
        if(game3.getPlacements() == null) {
            game3.setPlacements(new ArrayList<GamePlacement>());
        }
        game3.setId(3);
        game3.setParticipants(3);
        game3.addDeck(nivMizzet, 3);
        game3.addDeck(satoruGyruda, 1);
        game3.addDeck(chichiroEquip, 2);
        pod.addGame(game3);

        podCalculationService.CalculateRatingsForPod(pod, 1000);

        assertEquals(961, pod.getRatingForDeck(gontiTheft));
        assertEquals(921, pod.getRatingForDeck(roxanneAirstrike));
        assertEquals(1021, pod.getRatingForDeck(morskaClues));
        assertEquals(1091, pod.getRatingForDeck(chichiroEquip));
        assertEquals(1081, pod.getRatingForDeck(samwiseLifegain));
        assertEquals(1022, pod.getRatingForDeck(satoruGyruda));
        assertEquals(980, pod.getRatingForDeck(felixFiveBoots));
        assertEquals(980, pod.getRatingForDeck(dinoTribal));
        assertEquals(961, pod.getRatingForDeck(nivMizzet));
    }

}
