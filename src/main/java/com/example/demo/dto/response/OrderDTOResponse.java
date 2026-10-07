package com.example.demo.dto.response;

import com.example.demo.entities.Status;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
public class OrderDTOResponse {

    private Long id;
    private Long userId;
    private Long addressId;
    private Status status;
    private BigDecimal totalPrice;
    private Instant createdAt;
    private Instant updatedAt;
}
