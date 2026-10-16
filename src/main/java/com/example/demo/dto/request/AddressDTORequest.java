package com.example.demo.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class AddressDTORequest {

    private String city;
    private String street;
    private String houseNumber;
    private String  apartment;
    private String postalCode;
}
