package com.example.demo.dto.response;

import com.example.demo.entities.ActiveStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
public class ProductFilterDTOResponse {

    private Long id;

    private String name;

    private String description;

    private ProductTypeFilterDTOResponse productTypeFilterDTOResponse;

    private VendorFilterDTOResponse vendorFilterDTOResponse;

    private BigDecimal price;

    private ActiveStatus status;

    private Instant createdAt;

    private String imageUrl;
}
