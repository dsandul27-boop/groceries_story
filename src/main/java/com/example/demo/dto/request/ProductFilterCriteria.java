
package com.example.demo.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductFilterCriteria {

    @Positive private Long categoryId;
    @Positive private Long productTypeId;
    @Positive private Long vendorId;

    @PositiveOrZero private BigDecimal minPrice;
    @PositiveOrZero private BigDecimal maxPrice;


    private boolean activeOnly = false;

    @AssertTrue(message = "minPrice must not be greater than maxPrice")
    public boolean isPriceRangeValid() {
        return minPrice == null || maxPrice == null || minPrice.compareTo(maxPrice) <= 0;
    }
}
