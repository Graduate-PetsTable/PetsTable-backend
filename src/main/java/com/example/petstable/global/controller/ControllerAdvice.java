package com.example.petstable.global.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.petstable.global.exception.PetsTableException;
import com.example.petstable.global.exception.message.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleInputFieldException(MethodArgumentNotValidException e, BindingResult result) {

        Map<String, List<String>> errorMap = new HashMap<>();

        for (FieldError error : result.getFieldErrors()) {
            String fieldName = error.getField();
            String message = error.getDefaultMessage();

            if (errorMap.containsKey(fieldName)) {
                errorMap.get(fieldName).add(message);
            } else {
                List<String> errors = new ArrayList<>();
                errors.add(message);
                errorMap.put(fieldName, errors);
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonException(HttpMessageNotReadableException e) {
        log.warn("Json Exception ErrMessage={}\n", e.getMessage());

        return ResponseEntity.badRequest().body(new ErrorResponse(9000, "Json 형식이 올바르지 않습니다."));
    }

    @ExceptionHandler(HttpMediaTypeException.class)
    public ResponseEntity<ErrorResponse> handleContentTypeException(HttpMediaTypeException e) {
        log.warn("ContentType Exception ErrMessage={}\n", e.getMessage());

        return ResponseEntity.badRequest().body(new ErrorResponse(9001, "ContentType 값이 올바르지 않습니다."));
    }

    @ExceptionHandler(PetsTableException.class)
    public ResponseEntity<ErrorResponse> handlePetsTableException(PetsTableException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(new ErrorResponse(e.getCode(), e.getMessage()));
    }
}
