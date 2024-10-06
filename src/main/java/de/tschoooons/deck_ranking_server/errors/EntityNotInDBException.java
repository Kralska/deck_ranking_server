package de.tschoooons.deck_ranking_server.errors;

public class EntityNotInDBException extends RuntimeException{
    public EntityNotInDBException(String message) {
        super(message);
    }
}
