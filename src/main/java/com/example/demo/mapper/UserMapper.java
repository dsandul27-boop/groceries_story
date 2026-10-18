package com.example.demo.mapper;

import com.example.demo.dto.request.UserDTORequest;
import com.example.demo.dto.response.UserDTOResponse;
import com.example.demo.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTOResponse toDTO(User user){
        UserDTOResponse userDTOResponse = new UserDTOResponse();
        userDTOResponse.setUserId(user.getId());
        userDTOResponse.setFirstName(user.getFirstName());
        userDTOResponse.setLastName(user.getLastName());
        userDTOResponse.setEmail(user.getEmail());
        userDTOResponse.setPassword(user.getPassword());
        userDTOResponse.setPhone(user.getPhone());
        userDTOResponse.setRole(user.getRole());
        userDTOResponse.setCreatedAt(user.getCreatedAt());
        userDTOResponse.setActive(user.isActive());
        return userDTOResponse;
    }

    public User toEntity(UserDTORequest userDTORequest){
        User user = new User();
        user.setFirstName(userDTORequest.getFirstName());
        user.setLastName(userDTORequest.getLastName());
        user.setEmail(userDTORequest.getEmail());
        user.setPassword(userDTORequest.getPassword());
        user.setPhone(userDTORequest.getPhone());
        user.setRole(userDTORequest.getRole());
        user.setCreatedAt(userDTORequest.getCreatedAt());
        user.setActive(userDTORequest.isActive());
        return user;
    }

}
