package com.example.demo.controller;

import com.example.demo.dto.request.UserDTORequest;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;



    public AuthController(AuthService authService) {
        this.authService = authService;

    }


    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestParam String email,
            @RequestParam String password
    ) {
        String token = authService.login(email, password);

        return ResponseEntity.ok(token);
    }


    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody UserDTORequest userDTORequest){
        User user = new User();
        user.setFirstName(userDTORequest.getFirstName());
        user.setLastName(userDTORequest.getLastName());
        user.setEmail(userDTORequest.getEmail());
        user.setPassword(userDTORequest.getPassword());
        user.setPhone(userDTORequest.getPhone());
        user.setRole(Role.CUSTOMER);

        authService.register(user);

        return ResponseEntity.status(201).build();
    }

}
