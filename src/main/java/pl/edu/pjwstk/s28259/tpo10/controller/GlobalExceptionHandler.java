package pl.edu.pjwstk.s28259.tpo10.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> errors = new HashMap<>();

        for( var error: ex.getBindingResult().getFieldErrors() ){
            String fullMessage = error.getDefaultMessage();

            var messageList = Arrays.asList(
                    Objects.requireNonNull(fullMessage)
                            .split(";")
            );

            if (messageList.size() == 1)
                errors.put( error.getField(), fullMessage);
            else
                errors.put( error.getField(), messageList);
        }


        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }
}
