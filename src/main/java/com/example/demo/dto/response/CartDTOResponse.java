package com.example.demo.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Data
@NoArgsConstructor
public class CartDTOResponse {

    private Long id;
    private Long userId;
    private Instant createdAt;
    private Instant updateAt;
}
