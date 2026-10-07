package com.example.demo.dto.response;

import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.ProductType;
import com.example.demo.entities.Vendor;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
public class ProductFilterDTOResponse {

    private Long id;

    private ProductTypeFilterDTOResponse productTypeFilterDTOResponse;

    private VendorFilterDTOResponse vendorFilterDTOResponse;

    private BigDecimal price;

    private ActiveStatus activeStatus;

    private Instant createdAt;
}
