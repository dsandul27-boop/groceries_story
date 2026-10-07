package com.example.demo.service;

import com.example.demo.dto.request.UserDTORequest;
import com.example.demo.dto.response.UserDTOResponse;
import com.example.demo.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void updateStatus(User user);
    List<UserDTOResponse> getAll();
    UserDTOResponse getById(Long userId);
    void delete(Long userId);
}
