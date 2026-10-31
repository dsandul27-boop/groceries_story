package com.example.demo.dto.response;

import com.example.demo.entities.ActiveStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ProductFilterDTOResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private ActiveStatus status;
    private Long availableQuantity;
    private RefDTO productType;   // { id, name }
    private RefDTO vendor;        // { id, name }
    private String imageUrl;
}
