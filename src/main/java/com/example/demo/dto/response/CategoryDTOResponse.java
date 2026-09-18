package com.example.demo.dto.response;

import com.example.demo.entities.ActiveStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryDTOResponse {

    private Long id;
    private String categoryName;
    private String description;
    private ActiveStatus status;
}
