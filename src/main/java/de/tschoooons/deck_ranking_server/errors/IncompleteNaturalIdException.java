package de.tschoooons.deck_ranking_server.errors;

public class IncompleteNaturalIdException extends RuntimeException{
    public IncompleteNaturalIdException(String message){
        super(message);
    }
}
