package com.example.demo.service;

import com.example.demo.dto.request.UserDTORequest;
import com.example.demo.dto.response.UserDTOResponse;

import java.util.List;

public interface UserService {

    List<UserDTOResponse> getAll();
    UserDTOResponse getById(Long userId);
    void delete(Long userId);
}
