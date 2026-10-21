package com.example.demo.controller;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.dto.response.ProductDTOResponse;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.Status;
import com.example.demo.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableMethodSecurity
@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductServiceImpl productService;

    @MockitoBean
    private JwtUtil jwtUtil;

    ProductDTORequest productDTORequest = new ProductDTORequest();
    ProductDTOResponse productDTOResponse = new ProductDTOResponse();
    private Long productId = 1L;

    @BeforeEach
    void setUp() {
        productId = 1L;

        productDTORequest = new ProductDTORequest();
        productDTORequest.setVendorId(1L);
        productDTORequest.setProductTypeId(1L);
        productDTORequest.setPrice(new BigDecimal("10"));

        productDTOResponse = new ProductDTOResponse();
        productDTOResponse.setId(1L);
        productDTOResponse.setVendorId(1L);
        productDTOResponse.setProductTypeId(1L);
        productDTOResponse.setPrice(new BigDecimal("10"));
        productDTOResponse.setStatus(ActiveStatus.INACTIVE);
    }


    @Test
    @WithMockUser(roles = "STAFF")
    void save_shouldReturn202() throws Exception {

        when(productService.save(productDTORequest)).thenReturn(productDTOResponse);

        mockMvc.perform(
                        post("/product/save-product")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {"vendorId":1,
                                        "productTypeId":1,
                                        "price":10}
                                        """)
                )
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.vendorId").value(1))
                .andExpect(jsonPath("$.productTypeId").value(1))
                .andExpect(jsonPath("$.status").value("INACTIVE"));

        verify(productService).save(any(ProductDTORequest.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getById_shouldReturn202() throws Exception {

        when(productService.getById(productId)).thenReturn(productDTOResponse);

        mockMvc.perform(get("/product/get-product-by-id/1")).andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.vendorId").value(1L))
                .andExpect(jsonPath("$.productTypeId").value(1L));

        verify(productService).getById(productId);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAll_shouldReturnAccepted() throws Exception {

        List<ProductDTOResponse> productDTOResponseList = new ArrayList<>();
        productDTOResponseList.add(productDTOResponse);

        when(productService.getAll()).thenReturn(productDTOResponseList);

        mockMvc.perform(get("/product/get-all-product"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].vendorId").value(1L));

        verify(productService).getAll();
    }


    @Test
    @WithMockUser(roles = "STAFF")
    void softDelete_shouldReturn200OkAsStaff() throws Exception {
        doNothing().when(productService).softDelete(productId);

        mockMvc.perform(delete("/product/soft-delete/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(productService).softDelete(productId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void hardDelete_shouldReturn204NoContentAsAdmin() throws Exception {
        doNothing().when(productService).hardDelete(productId);

        mockMvc.perform(delete("/product/hard-delete/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(productService).hardDelete(productId);
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void hardDelete_shouldReturn403ForbiddenAsStaff() throws Exception {

        mockMvc.perform(delete("/product/hard-delete/1").with(csrf()))
                .andExpect(status().isForbidden());

        verify(productService, never()).hardDelete(anyLong());
    }
}
