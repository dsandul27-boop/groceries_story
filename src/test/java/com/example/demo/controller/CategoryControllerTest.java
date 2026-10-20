package com.example.demo.controller;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.request.CategoryDTORequest;
import com.example.demo.dto.response.CategoryDTOResponse;
import com.example.demo.entities.Category;
import com.example.demo.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableMethodSecurity
@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryServiceImpl categoryService;


    @MockitoBean
    private JwtUtil jwtUtil;

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
    @WithMockUser(roles = "STAFF")
    void save_shouldReturn200() throws Exception {

        when(categoryService.save(categoryDTORequest)).thenReturn(categoryDTOResponse);

        mockMvc.perform(
                        post("/category/save-category")
                                .with(csrf())
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
    @WithMockUser(roles = "CUSTOMER")
    void getById_shouldReturn202() throws Exception {

        when(categoryService.getById(categoryId)).thenReturn(categoryDTOResponse);

        mockMvc.perform(get("/category/get-category-by-id/1")).andExpect(status().isAccepted()).
                andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("categoryName").value("Dairy"))
                .andExpect(jsonPath("$.description").value("milk"));

        verify(categoryService).getById(categoryId);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAll_shouldReturnAccepted() throws Exception {

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
    @WithMockUser(roles = "STAFF")
    void softDelete_shouldReturn200OkAsStaff() throws Exception {
        doNothing().when(categoryService).softDelete(categoryId);

        mockMvc.perform(delete("/category/soft-delete/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(categoryService).softDelete(categoryId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void hardDelete_shouldReturn204NoContentAsAdmin() throws Exception {
        doNothing().when(categoryService).hardDelete(categoryId);

        mockMvc.perform(delete("/category/hard-delete/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(categoryService).hardDelete(categoryId);
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void hardDelete_shouldReturn403ForbiddenAsStaff() throws Exception {
        mockMvc.perform(delete("/category/hard-delete/1").with(csrf()))
                .andExpect(status().isForbidden());

        verify(categoryService, never()).hardDelete(anyLong());
    }
}
