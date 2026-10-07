package com.example.demo.dto.response;

import com.example.demo.entities.ActiveStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
public class ProductDTOResponse {

    private Long id;
    private Long vendorId;
    private Long productTypeId;
    private BigDecimal price;
    private ActiveStatus activeStatus;
    private Instant createdAt;
    private Long quantity;

}
