package com.example.demo.controller;

import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.dto.response.ProductDTOResponse;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductServiceImpl productService;

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
        productDTOResponse.setActiveStatus(ActiveStatus.INACTIVE);
    }

    @Test
    void save_shouldReturn202() throws Exception {

        when(productService.save(productDTORequest)).thenReturn(productDTOResponse);

        mockMvc.perform(
                        post("/product/save-product")
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
                .andExpect(jsonPath("$.activeStatus").value("INACTIVE"));

        verify(productService).save(any(ProductDTORequest.class));
    }

    @Test
    void getById_shouldReturn202() throws Exception {

        when(productService.getById(productId)).thenReturn(productDTOResponse);

        mockMvc.perform(get("/product/get-product-by-id/1")).andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.vendorId").value(1L))
                .andExpect(jsonPath("$.productTypeId").value(1L));

        verify(productService).getById(productId);
    }

    @Test
    void getAll_shouldReturnAccepted() throws Exception {

        List<ProductDTOResponse> productDTOResponseList = new ArrayList<>();
        productDTOResponseList.add(productDTOResponse);

        when(productService.getAll()).thenReturn(productDTOResponseList);

        mockMvc.perform(get("/product/get-all-product")).andExpect(status().isAccepted())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].vendorId").value(1L));
        verify(productService).getAll();
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        doNothing().when(productService).delete(productId);

        mockMvc.perform(delete("/product/delete-by-id/1")).andExpect(status().isNoContent());

        verify(productService).delete(productId);
    }
}
