package com.example.demo.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class CartDTORequest {
    private Long userId;
}
