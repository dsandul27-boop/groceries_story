package com.example.demo.exception;

public class PasswordAuthentification extends RuntimeException {
    public PasswordAuthentification(String message) {
        super(message);
    }
}
