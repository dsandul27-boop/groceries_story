package com.example.demo.dto.response;

import com.example.demo.entities.ActiveStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
public class BatchFilterDTOResponse {

    private Long quantity;

    private BigDecimal purchasePrice;

    private Instant expirationDate;

    private ActiveStatus activeStatus;

    private Instant createdAt;

}
