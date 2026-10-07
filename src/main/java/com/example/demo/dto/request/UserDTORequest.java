package com.example.demo.dto.request;

import com.example.demo.entities.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class UserDTORequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private Long addressId;
    private Role role;
    private Instant createdAt;
    private boolean isActive;
}
