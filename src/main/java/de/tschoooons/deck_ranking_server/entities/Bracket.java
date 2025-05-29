package de.tschoooons.deck_ranking_server.entities;

import lombok.Getter;

/**
 * Commander brackets.
 * 
 * <p> Brackets are a tier system designed to encourage pre-game discussion by providing a set if 
 * guidelines to identify and rate a decks relative strength.
 */
@Getter
public enum Bracket {
    /** Decks that prioritize theme over function, showcase a unique idea or value experience over 
     * winning 
     */
    EXHIBITION(1),
    /** Focused decks, but not every card must be best in slot. Comparable to average precon. */
    CORE(2),
    /** Thoughtfully designed decks with strong and synergistic cards. Games can end out of 
     * nowhere 
     */
    UPGRADED(3),
    /**
     * Decks with the most powerful cards and able to play (and win) against anything.
     */
    OPTIMIZED(4),
    /**
     * Anything goes, with a focus on being competitive with the other strongest decks.
     */
    CEDH(5);

    Bracket(int tier) {this.tier = tier;};

    private final int tier;

    
}
