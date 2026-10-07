package com.example.demo.dto.response;

import com.example.demo.entities.ActiveStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VendorFilterDTOResponse {

    private Long id;

    private String name;

    private ActiveStatus activeStatus;
}
