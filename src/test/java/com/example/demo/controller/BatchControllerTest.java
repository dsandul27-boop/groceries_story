package com.example.demo.controller;


import com.example.demo.config.JwtUtil;
import com.example.demo.dto.request.BatchDTORequest;
import com.example.demo.dto.response.BatchDTOResponse;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.service.impl.BatchServiceImpl;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@EnableMethodSecurity
@WebMvcTest(BatchController.class)
public class BatchControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BatchServiceImpl batchService;

    @MockitoBean
    private JwtUtil jwtUtil;

    BatchDTORequest batchDTORequest = new BatchDTORequest();
    BatchDTOResponse batchDTOResponse = new BatchDTOResponse();
    private Long batchId = 1L;
    private final Instant expirationDate = Instant.parse("2024-06-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        batchId = 1L;

        batchDTORequest = new BatchDTORequest();
        batchDTORequest.setQuantity(5L);
        batchDTORequest.setPurchasePrice(new BigDecimal("12.50"));
        batchDTORequest.setExpirationDate(expirationDate);
        batchDTORequest.setProductId(1L);

        batchDTOResponse = new BatchDTOResponse();
        batchDTOResponse.setId(1L);
        batchDTOResponse.setQuantity(5L);
        batchDTOResponse.setPurchasePrice(new BigDecimal("12.50"));
        batchDTOResponse.setExpirationDate(expirationDate);
        batchDTOResponse.setStatus(ActiveStatus.ACTIVE);
        batchDTOResponse.setProductId(1L);
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void save_shouldReturn201() throws Exception {

        when(batchService.save(batchDTORequest)).thenReturn(batchDTOResponse);

        mockMvc.perform(
                        post("/batch/save-batch")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {"quantity":5,
                                        "purchasePrice":12.50,
                                        "expirationDate":"2024-06-01T00:00:00Z",
                                        "productId":1}
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(5))
                .andExpect(jsonPath("$.productId").value(1));

        verify(batchService).save(any(BatchDTORequest.class));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void getById_shouldReturn200() throws Exception {

        when(batchService.getById(batchId)).thenReturn(batchDTOResponse);

        mockMvc.perform(get("/batch/get-batch-by-id/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.quantity").value(5));

        verify(batchService).getById(batchId);
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void getAll_shouldReturnAccepted() throws Exception {

        List<BatchDTOResponse> batchDTOResponseList = new ArrayList<>();
        batchDTOResponseList.add(batchDTOResponse);

        when(batchService.getAll()).thenReturn(batchDTOResponseList);

        mockMvc.perform(get("/batch/get-all-batch")).andExpect(status().isAccepted())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));
        verify(batchService).getAll();
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void softDelete_shouldReturn200AsStaff() throws Exception {
        doNothing().when(batchService).softDelete(batchId);

        mockMvc.perform(delete("/batch/soft-delete/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(batchService).softDelete(batchId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void hardDelete_shouldReturn202AsAdmin() throws Exception {
        doNothing().when(batchService).hardDelete(batchId);

        mockMvc.perform(delete("/batch/hard-delete/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(batchService).hardDelete(batchId);
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void hardDelete_shouldReturn403AsStaff() throws Exception {
        mockMvc.perform(delete("/batch/hard-delete/1").with(csrf()))
                .andExpect(status().isForbidden());

        verify(batchService, never()).hardDelete(anyLong());
    }
}
