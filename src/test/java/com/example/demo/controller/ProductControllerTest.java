package com.example.demo.controller;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.dto.request.ProductUpdateDTORequest;
import com.example.demo.dto.response.ProductDTOResponse;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.exception.ResourceNotFoundException;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
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
    ProductUpdateDTORequest productUpdateDTORequest = new ProductUpdateDTORequest();

    private Long productId = 1L;

    @BeforeEach
    void setUp() {
        productId = 1L;

        productDTORequest = new ProductDTORequest();
        productDTORequest.setName("Latte");
        productDTORequest.setDescription("Very tasty milk, trust me");
        productDTORequest.setVendorId(1L);
        productDTORequest.setProductTypeId(1L);
        productDTORequest.setPrice(new BigDecimal("10"));

        productDTOResponse = new ProductDTOResponse();
        productDTOResponse.setId(1L);
        productDTOResponse.setName("Latte");
        productDTOResponse.setDescription("Very tasty milk, trust me");
        productDTOResponse.setVendorId(1L);
        productDTOResponse.setProductTypeId(1L);
        productDTOResponse.setPrice(new BigDecimal("10"));
        productDTOResponse.setStatus(ActiveStatus.INACTIVE);

        productUpdateDTORequest = new ProductUpdateDTORequest();
        productUpdateDTORequest.setName("Latte");
        productUpdateDTORequest.setDescription("Very tasty milk, trust me");
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void save_shouldReturn202Accepted() throws Exception {
        // Fix 1: Use any() to match Jackson's deserialized instance on POST
        when(productService.save(any(ProductDTORequest.class))).thenReturn(productDTOResponse);

        mockMvc.perform(
                        post("/product/save-product")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {"name":"Latte",
                                        "description":"Very tasty milk, trust me",
                                        "vendorId":1,
                                        "productTypeId":1,
                                        "price":10}
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Latte"))
                .andExpect(jsonPath("$.description").value("Very tasty milk, trust me"))
                .andExpect(jsonPath("$.vendorId").value(1))
                .andExpect(jsonPath("$.productTypeId").value(1))
                .andExpect(jsonPath("$.price").value(10))
                .andExpect(jsonPath("$.status").value("INACTIVE"));

        verify(productService).save(any(ProductDTORequest.class));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void save_shouldReturn400() throws Exception {
        mockMvc.perform(
                        post("/product/save-product")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {"name":"  ",
                                        "description":"Very tasty milk, trust me",
                                        "vendorId":1,
                                        "productTypeId":1,
                                        "price":10}
                                        """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void save_shouldReturn202_whenDescriptionIsMissing() throws Exception {
        ProductDTOResponse dtoResponseWithoutDescription = new ProductDTOResponse();
        dtoResponseWithoutDescription.setId(1L);
        dtoResponseWithoutDescription.setName("Latte");
        dtoResponseWithoutDescription.setDescription(null);
        dtoResponseWithoutDescription.setVendorId(1L);
        dtoResponseWithoutDescription.setProductTypeId(1L);
        dtoResponseWithoutDescription.setPrice(new BigDecimal("10"));
        dtoResponseWithoutDescription.setStatus(ActiveStatus.INACTIVE);

        when(productService.save(any(ProductDTORequest.class))).thenReturn(dtoResponseWithoutDescription);

        mockMvc.perform(
                        post("/product/save-product")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "name": "Latte",
                                            "vendorId": 1,
                                            "productTypeId": 1,
                                            "price": 10
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Latte"))
                .andExpect(jsonPath("$.description").value(org.hamcrest.Matchers.nullValue()))
                .andExpect(jsonPath("$.vendorId").value(1))
                .andExpect(jsonPath("$.productTypeId").value(1))
                .andExpect(jsonPath("$.price").value(10))
                .andExpect(jsonPath("$.status").value("INACTIVE"));

        verify(productService).save(any(ProductDTORequest.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getById_shouldReturn200() throws Exception {
        when(productService.getById(productId)).thenReturn(productDTOResponse);

        mockMvc.perform(get("/product/get-product-by-id/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Latte"))
                .andExpect(jsonPath("$.description").value("Very tasty milk, trust me"))
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(productService).getAll();
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void softDelete_shouldReturn204NoContentAsStaff() throws Exception {
        // Fix 2: Renamed test method to perfectly match the 204 status check
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

    // TESTS FOR UPDATE (PATCH)

    @Test
    @WithMockUser(roles = {"STAFF"})
    void update_shouldReturn200AndUpdatedBody_whenDataIsValid() throws Exception {
        when(productService.updateProduct(eq(productId), any(ProductUpdateDTORequest.class)))
                .thenReturn(productDTOResponse);

        mockMvc.perform(
                        patch("/product/update-product/{productId}", productId).contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                        "name": "Latte",
                                        "description": "Very tasty milk, trust me"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Latte"))
                .andExpect(jsonPath("$.description").value("Very tasty milk, trust me"));
        verify(productService).updateProduct(eq(productId), any(ProductUpdateDTORequest.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void update_shouldReturn404_whenProductIdDoesNotExist() throws Exception {
        when(productService.updateProduct(eq(999L), any(ProductUpdateDTORequest.class)))
                .thenThrow(new ResourceNotFoundException("Product was not found"));
        mockMvc.perform(
                        patch("/product/update-product/{productId}", 999L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                        "name": "Latte",
                                        "description": "Very tasty milk, trust me"
                                        }
                                        """)
                )
                .andExpect(status().isNotFound());
        verify(productService).updateProduct(eq(999L), any(ProductUpdateDTORequest.class));
    }



    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void update_shouldReturn403_whenUserIsCustomer() throws Exception {
        mockMvc.perform(
                        patch("/product/update-product/{productId}", productId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                        "name": "Latte",
                                        "description": "Very tasty milk, trust me"
                                        }
                                        """)
                )
                .andExpect(status().isForbidden());
        verifyNoInteractions(productService);
    }
}
