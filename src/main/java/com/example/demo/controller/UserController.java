package com.example.demo.controller;
import com.example.demo.dto.request.UserDTORequest;
import com.example.demo.dto.response.UserDTOResponse;
import com.example.demo.entities.User;
import com.example.demo.service.AuthService;
import com.example.demo.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserServiceImpl userService;
    private final AuthService authService;

    public UserController(UserServiceImpl userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-users")
    public ResponseEntity<List<UserDTOResponse> >getAllUsers(){
        List<UserDTOResponse> userDTOResponseList = userService.getAll();
        return new ResponseEntity<>(userDTOResponseList, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-user-by-id")
    public ResponseEntity<UserDTOResponse> getUserById(@RequestParam @NotNull @Positive(message = "Can't be null") Long userId){
        UserDTOResponse userDTOResponse = userService.getById(userId);
        return new ResponseEntity<>(userDTOResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-user-by-id/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable @NotNull @Positive(message = "Can't be null") Long userId){
        userService.delete(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PostMapping("/create")
    public ResponseEntity<UserDTOResponse> masterRegister(@Valid @RequestBody UserDTORequest userDTORequest){
        User user = new User();
        user.setFirstName(userDTORequest.getFirstName());
        user.setLastName(userDTORequest.getLastName());
        user.setEmail(userDTORequest.getEmail());
        user.setPassword(userDTORequest.getPassword());
        user.setPhone(userDTORequest.getPhone());
        user.setRole(userDTORequest.getRole());

        UserDTOResponse dtoResponse = authService.register(user);
        return new ResponseEntity<>(dtoResponse, HttpStatus.CREATED);
    }

}
