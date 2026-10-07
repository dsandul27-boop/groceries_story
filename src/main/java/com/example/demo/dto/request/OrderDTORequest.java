package com.example.demo.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class OrderDTORequest {
    private Long addressId;
    private BigDecimal totalPrice;
    private Long userId;

}
