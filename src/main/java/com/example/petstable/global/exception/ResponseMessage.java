package com.example.petstable.global.exception;

import org.springframework.http.HttpStatus;

public interface ResponseMessage {

    String getMessage();
    HttpStatus getStatus();
}
