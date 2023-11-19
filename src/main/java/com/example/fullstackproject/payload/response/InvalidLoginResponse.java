package com.example.fullstackproject.payload.response;

import lombok.Getter;

@Getter
public class InvalidLoginResponse {

    private final String message = "Email or password invalid";
    private final int statusCode = 401;
}
