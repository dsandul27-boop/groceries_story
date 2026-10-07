package com.example.demo.controller;

import com.example.demo.dto.request.UserDTORequest;
import com.example.demo.dto.response.UserDTOResponse;
import com.example.demo.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }



    @PreAuthorize("ROLE('ADMIN')")
    @GetMapping("/get-users")
    public ResponseEntity<List<UserDTOResponse> >getAllUsers(){
        List<UserDTOResponse> userDTOResponseList = userService.getAll();
        return new ResponseEntity<>(userDTOResponseList, HttpStatus.OK);
    }

    @PreAuthorize("ROLE('ADMIN')")
    @GetMapping("/get-user-by-id")
    public ResponseEntity<UserDTOResponse> getUserById(@RequestParam @NotNull @Positive(message = "Can't be null") Long userId){
        UserDTOResponse userDTOResponse = userService.getById(userId);
        return new ResponseEntity<>(userDTOResponse, HttpStatus.OK);
    }

    @PreAuthorize("ROLE('ADMIN')")
    @DeleteMapping("/save-user")
    public ResponseEntity<Void> deleteUser(@RequestParam @NotNull @Positive(message = "Can't be null") Long userId){
        userService.delete(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
