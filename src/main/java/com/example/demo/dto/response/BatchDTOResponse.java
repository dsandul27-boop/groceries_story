package com.example.demo.dto.response;

import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.Product;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
public class BatchDTOResponse {

    private Long id;
    private Long quantity;
    private BigDecimal purchasePrice;
    private Instant expirationDate;
    private ActiveStatus activeStatus;
    private Instant createdAt;
    private Long productId;
}
