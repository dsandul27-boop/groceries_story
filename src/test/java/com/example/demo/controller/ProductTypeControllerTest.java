package com.example.demo.controller;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.request.ProductTypeDTORequest;
import com.example.demo.dto.response.ProductTypeDTOResponse;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.service.impl.ProductTypeServiceImpl;
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
@WebMvcTest(ProductTypeController.class)
public class ProductTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductTypeServiceImpl productTypeService;

    @MockitoBean
    private JwtUtil jwtUtil;

    ProductTypeDTORequest productTypeDTORequest = new ProductTypeDTORequest();
    ProductTypeDTOResponse productTypeDTOResponse = new ProductTypeDTOResponse();
    private Long productTypeId = 1L;

    @BeforeEach
    void setUp() {
        productTypeId = 1L;

        productTypeDTORequest = new ProductTypeDTORequest();
        productTypeDTORequest.setName("Milk");
        productTypeDTORequest.setDescription("Dairy stuff");
        productTypeDTORequest.setCategoryId(1L);

        productTypeDTOResponse = new ProductTypeDTOResponse();
        productTypeDTOResponse.setId(1L);
        productTypeDTOResponse.setName("Milk");
        productTypeDTOResponse.setDescription("Dairy stuff");
        productTypeDTOResponse.setCategoryId(1L);
        productTypeDTOResponse.setStatus(ActiveStatus.ACTIVE);
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void save_shouldReturn201() throws Exception {
        when(productTypeService.save(any(ProductTypeDTORequest.class))).thenReturn(productTypeDTOResponse);


        mockMvc.perform(
                        post("/product-type")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {"name":"Milk",
                                        "description":"Dairy stuff",
                                        "categoryId":1}
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Milk"))
                .andExpect(jsonPath("$.categoryId").value(1));

        verify(productTypeService).save(any(ProductTypeDTORequest.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getById_shouldReturn200() throws Exception {
        when(productTypeService.getById(productTypeId)).thenReturn(productTypeDTOResponse);

        mockMvc.perform(get("/product-type/{productTypeId}", productTypeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Milk"));

        verify(productTypeService).getById(productTypeId);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAll_shouldReturn200() throws Exception {
        List<ProductTypeDTOResponse> productTypeDTOResponseList = new ArrayList<>();
        productTypeDTOResponseList.add(productTypeDTOResponse);

        when(productTypeService.getAll()).thenReturn(productTypeDTOResponseList);


        mockMvc.perform(get("/product-type/get-all-product-type"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(productTypeService).getAll();
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void softDelete_shouldReturn204AsStaff() throws Exception {
        doNothing().when(productTypeService).softDelete(productTypeId);

        mockMvc.perform(delete("/product-type/soft-delete/{productTypeId}", productTypeId).with(csrf()))
                .andExpect(status().isNoContent());

        verify(productTypeService).softDelete(productTypeId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void hardDelete_shouldReturn204NoContentAsAdmin() throws Exception {
        doNothing().when(productTypeService).hardDelete(productTypeId);


        mockMvc.perform(delete("/product-type/hard-delete/{productTypeId}", productTypeId).with(csrf()))
                .andExpect(status().isNoContent());

        verify(productTypeService).hardDelete(productTypeId);
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void hardDelete_shouldReturn403ForbiddenAsStaff() throws Exception {

        mockMvc.perform(delete("/product-type/hard-delete/{productTypeId}", productTypeId).with(csrf()))
                .andExpect(status().isForbidden());

        verify(productTypeService, never()).hardDelete(anyLong());
    }
}
