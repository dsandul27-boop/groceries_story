package com.example.demo.service;

import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;

public interface AuthService {
    String login(String email, String password);
    void register(User user);
}
