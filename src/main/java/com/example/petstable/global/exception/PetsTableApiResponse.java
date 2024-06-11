package com.example.petstable.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PetsTableApiResponse<T> {
    private T data;
    private String message;
    private String code;

    public static <G> PetsTableApiResponse<G> createResponse(G data, ResponseMessage responseMessage) {
        return new PetsTableApiResponse<>(data, responseMessage.getMessage(), responseMessage.toString());
    }
}