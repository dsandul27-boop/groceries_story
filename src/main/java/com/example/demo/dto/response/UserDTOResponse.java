package com.example.demo.dto.response;

import com.example.demo.entities.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class UserDTOResponse {

    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private Role role;
    private Instant createdAt;
    private boolean isActive;
}
