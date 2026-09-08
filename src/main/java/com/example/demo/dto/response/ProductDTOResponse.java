package com.example.demo.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ProductDTOResponse {

    private Long id;
    private Long vendorId;
    private Long productTypeId;
    private BigDecimal price;
    private Boolean active;
    private LocalDateTime createdAt;

}
