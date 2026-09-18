package com.example.demo.dto.response;

import com.example.demo.entities.ActiveStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductTypeDTOResponse {

    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private ActiveStatus status;
}
