package com.example.demo.dto.response;

import com.example.demo.entities.ActiveStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VendorFilterDTOResponse {

    private Long id;

    private String name;

    private ActiveStatus activeStatus;
}
