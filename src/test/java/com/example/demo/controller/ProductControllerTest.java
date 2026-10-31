package com.example.demo.controller;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.dto.request.ProductFilterCriteria;
import com.example.demo.dto.request.ProductUpdateDTORequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.ProductDTOResponse;
import com.example.demo.dto.response.ProductFilterDTOResponse;
import com.example.demo.dto.response.RefDTO;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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

    private MockMultipartFile createImage() {
        return new MockMultipartFile(
                "file",
                "milk.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[]{1, 2, 3}
        );
    }

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

    //TEST FOR IMAGE UPLOAD AND DELETE

    @Test
    @WithMockUser(roles = {"STAFF"})
    void uploadProductImage_authorized_returns200() throws Exception {
        MockMultipartFile file = createImage();


        when(productService.uploadProductImage(any(Long.class), any(MultipartFile.class)))
                .thenReturn(new ProductDTOResponse());

        mockMvc.perform(multipart("/product/upload-product-image/{productId}", 1L)
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(productService, times(1)).uploadProductImage(any(Long.class), any(MultipartFile.class));
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void uploadProductImage_forbiddenForCustomer_returns403() throws Exception {
        MockMultipartFile file = createImage();

        mockMvc.perform(multipart("/product/upload-product-image/{productId}", 1L)
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(productService);
    }

    @Test
    @WithMockUser(roles = {"STAFF"})
    void uploadProductImage_badContentType_returns400() throws Exception {
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file", "test.txt", "text/plain", "invalid data".getBytes()
        );


        when(productService.uploadProductImage(any(Long.class), any(MultipartFile.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image format"));

        mockMvc.perform(multipart("/product/upload-product-image/{productId}", 1L)
                        .file(invalidFile)
                        .with(csrf()))
                .andExpect(status().isBadRequest()); // Acum va primi 400
    }



    @Test
    @WithMockUser(roles = {"STAFF"})
    void deleteProductImage_asStaff_returns200() throws Exception {
        when(productService.deleteProductImage(1L))
                .thenReturn(productDTOResponse);

        mockMvc.perform(delete(
                        "/product/delete-product-image/{productId}", 1L)
                        .with(user("staff").roles("STAFF"))
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(productService).deleteProductImage(1L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteProductImage_asAdmin_returns200() throws Exception {
        when(productService.deleteProductImage(1L))
                .thenReturn(productDTOResponse);

        mockMvc.perform(delete(
                        "/product/delete-product-image/{productId}", 1L)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(productService).deleteProductImage(1L);
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void deleteProductImage_asCustomer_returns403() throws Exception {
        mockMvc.perform(delete(
                        "/product/delete-product-image/{productId}", 1L)
                        .with(user("customer").roles("CUSTOMER"))
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(productService);
    }

    @Test
    void deleteProductImage_unauthenticated_returns401() throws Exception {
        mockMvc.perform(delete(
                        "/product/delete-product-image/{productId}", 1L)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(productService);
    }

    @Test
    void givenValidParams_whenGetProductFilter_thenReturn200AndFlattenedJsonStructure() throws Exception {
        ProductFilterDTOResponse dto = new ProductFilterDTOResponse();
        dto.setId(10L);
        dto.setName("Ulei de Floarea Soarelui");
        dto.setDescription("Ulei presat la rece 1L");
        dto.setPrice(BigDecimal.valueOf(11.50));
        dto.setStatus(ActiveStatus.ACTIVE);
        dto.setAvailableQuantity(85L);
        dto.setProductType(new RefDTO(2L, "Uleiuri"));
        dto.setVendor(new RefDTO(3L, "Floriol"));
        dto.setImageUrl("http://image.url");

        PageResponse<ProductFilterDTOResponse> pageResponse = new PageResponse<>();
        pageResponse.setContent(List.of(dto));
        pageResponse.setTotalElements(1);

        when(productService.getProductFilter(any(ProductFilterCriteria.class), any(Pageable.class)))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/product/get-product-filter")
                        .param("categoryId", "1")
                        .param("minPrice", "5.00")
                        .param("maxPrice", "15.00")
                        .param("activeOnly", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].name").value("Ulei de Floarea Soarelui"))
                .andExpect(jsonPath("$.content[0].description").value("Ulei presat la rece 1L"))
                .andExpect(jsonPath("$.content[0].price").value(11.50))
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.content[0].availableQuantity").value(85))
                .andExpect(jsonPath("$.content[0].productType.id").value(2))
                .andExpect(jsonPath("$.content[0].productType.name").value("Uleiuri"))
                .andExpect(jsonPath("$.content[0].vendor.id").value(3))
                .andExpect(jsonPath("$.content[0].vendor.name").value("Floriol"))
                .andExpect(jsonPath("$.content[0].imageUrl").value("http://image.url"));
    }

    @Test
    void givenInvertedPriceRange_whenGetProductFilter_thenReturn400BadRequest() throws Exception {
        mockMvc.perform(get("/product/get-product-filter")
                        .param("minPrice", "15.00")
                        .param("maxPrice", "5.00") // min > max -> 400!
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
