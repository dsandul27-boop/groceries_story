package com.example.demo.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class AddressResponse {

    private Long id;
    private Long userId;
    private String city;
    private String street;
    private String houseNumber;
    private String  apartment;
    private String postalCode;
}
