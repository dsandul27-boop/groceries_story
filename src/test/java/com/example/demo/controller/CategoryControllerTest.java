package com.example.demo.controller;

import com.example.demo.dto.request.CategoryDTORequest;
import com.example.demo.dto.request.VendorDTORequest;
import com.example.demo.dto.response.CategoryDTOResponse;
import com.example.demo.entities.Category;
import com.example.demo.service.impl.CategoryServiceImpl;
import com.example.demo.service.impl.VendorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryServiceImpl categoryService;

    CategoryDTORequest categoryDTORequest = new CategoryDTORequest();
    Category category = new Category();
    CategoryDTOResponse categoryDTOResponse = new CategoryDTOResponse();
    private Long categoryId = 1L;

    @BeforeEach
    void setUp() {
        categoryId = 1L;

        categoryDTORequest = new CategoryDTORequest();
        categoryDTORequest.setCategoryName("Dairy");
        categoryDTORequest.setDescription("milk");

        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Dairy");
        category.setDescription("milk");

        categoryDTOResponse = new CategoryDTOResponse();
        categoryDTOResponse.setId(1L);
        categoryDTOResponse.setCategoryName("Dairy");
        categoryDTOResponse.setDescription("milk");

    }


    @Test
    void save_shouldReturn200() throws Exception{

        when(categoryService.save(categoryDTORequest)).thenReturn(categoryDTOResponse);

        mockMvc.perform(
                        post("/category/save-category")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {"categoryName":"Dairy",
                            "description" : "milk"}
                            """)
                )
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.categoryName").value("Dairy")).andExpect(jsonPath("$.description").value("milk"));

        verify(categoryService).save(any(CategoryDTORequest.class));
    }

    @Test
    void getById_shouldReturn202() throws  Exception{

        when(categoryService.getById(categoryId)).thenReturn(categoryDTOResponse);

        mockMvc.perform(get("/category/get-category-by-id/1")).andExpect(status().isAccepted()).
                andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("categoryName").value("Dairy"))
                .andExpect(jsonPath("$.description").value("milk"));

        verify(categoryService).getById(categoryId);
    }

    @Test
    void getAll_shouldReturnAccepted() throws  Exception{

        List<CategoryDTOResponse> categoryDTOResponseList = new ArrayList<>();
        categoryDTOResponseList.add(categoryDTOResponse);

        when(categoryService.getAll()).thenReturn(categoryDTOResponseList);

        mockMvc.perform(get("/category/get-all-category")).andExpect(status().isAccepted())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].categoryName").value("Dairy"))
                .andExpect(jsonPath("$[0].description").value("milk"));
        verify(categoryService).getAll();
    }

    @Test
    void delete_shouldReturnNoContent() throws  Exception {
        doNothing().when(categoryService).delete(categoryId);

        mockMvc.perform(delete("/category/delete-category/1")).andExpect(status().isNoContent());

        verify(categoryService).delete(categoryId);
    }
}
