package com.example.demo.service.impl;

import com.example.demo.dto.response.UserDTOResponse;
import com.example.demo.entities.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AddressRepository addressRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.addressRepository = addressRepository;
    }

    @Override
    public void updateStatus(Optional<User> optionalUser) {
        optionalUser.get().setActive(false);
        userRepository.save(optionalUser.get());

    }

    @Override
    public List<UserDTOResponse> getAll() {
        List<User> list = userRepository.findAll();
        if (list.isEmpty()) {
            throw new ResourceNotFoundException("No one user found");
        }
        List<UserDTOResponse> userDTOResponseList = new ArrayList<>();
        for (User user : list) {
            userDTOResponseList.add(userMapper.toDTO(user));
        }
        return userDTOResponseList;
    }

    @Override
    public UserDTOResponse getById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found");

        }
        return userMapper.toDTO(optionalUser.get());
    }

    @Override
    public void delete(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("Already doesn't exist");
        }
        updateStatus(optionalUser);
    }
}
