package com.example.demo.dto.request;

import com.example.demo.entities.ActiveStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VendorDTORequest {

    @NotBlank(message = "Missing filed categoryName")
    private String name;
}
