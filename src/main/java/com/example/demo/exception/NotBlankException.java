package com.example.demo.exception;

public class NotBlankException extends RuntimeException {
    public NotBlankException(String message) {
        super(message);
    }
}
