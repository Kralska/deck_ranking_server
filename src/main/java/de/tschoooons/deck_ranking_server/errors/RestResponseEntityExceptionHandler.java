package de.tschoooons.deck_ranking_server.errors;

import java.util.Map;
import java.util.HashMap;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler 
extends ResponseEntityExceptionHandler{

    @ExceptionHandler(value = 
    {EntityNotInDBException.class})
    protected ResponseEntity<Object> handleConflict(
        RuntimeException ex, WebRequest request
    ) {
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = 
    {IncompleteNaturalIdException.class})
    protected ResponseEntity<Object> handleConflict(
        IncompleteNaturalIdException ex, WebRequest request
    ) {
        String reason = "Incomplete id: " + ex.getMessage();
        return handleExceptionInternal(ex, reason, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = 
    {DataIntegrityViolationException.class})
    protected ResponseEntity<Object> handleConflict(
        DataIntegrityViolationException ex, WebRequest request
    ) {
        String reason = "Duplicate natural id. An entity with the given id already exists.";
        return handleExceptionInternal(ex, reason, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = 
    {InvalidDataAccessApiUsageException.class})
    protected ResponseEntity<Object> handleConflict(
        InvalidDataAccessApiUsageException ex, WebRequest request
    ) {
        String reason = "Ids must not be null.";
        return handleExceptionInternal(ex, reason, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex, 
            @NonNull HttpHeaders headers, 
            @NonNull HttpStatusCode status, 
            @NonNull WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.ok(errors);
    }

}
