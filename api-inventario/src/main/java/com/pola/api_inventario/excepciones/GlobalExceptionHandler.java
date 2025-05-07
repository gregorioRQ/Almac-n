package com.pola.api_inventario.excepciones;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pola.api_inventario.rest.models.RespuestaError;

import jakarta.persistence.EntityNotFoundException;

/*
 * Maneja de manera centralizada todas la excepciones 
 * que se generen a lo largo de toda la app.
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    // para manejar las excepciones arrojadas por las anotaciones de constraint
    // validations.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<String> handleItemNotFoundException(ItemNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(CantidadInsuficienteException.class)
    public ResponseEntity<String> handleInsufficientQuantityException(CantidadInsuficienteException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrió un error inesperado: " + ex.getMessage());
    }

    // Manejar EntityNotFoundException
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<RespuestaError> handleEntityNotFoundException(EntityNotFoundException ex) {
        RespuestaError respuestaError = new RespuestaError("No encontrado", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RespuestaError> handle(IllegalArgumentException ex) {
        RespuestaError respuestaError = new RespuestaError("Campo incorrecto", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaError);
    }

}
