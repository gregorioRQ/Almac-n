package com.pola.api_inventario.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pola.api_inventario.rest.models.RespuestaError;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

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

}
