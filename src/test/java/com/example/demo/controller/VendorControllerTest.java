package com.example.demo.controller;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.request.VendorDTORequest;
import com.example.demo.dto.response.VendorDTOResponse;
import com.example.demo.service.impl.VendorServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableMethodSecurity
@WebMvcTest(VendorController.class)
public class VendorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VendorServiceImpl vendorService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getById_shouldReturn200() throws Exception {

        VendorDTOResponse dto = new VendorDTOResponse();
        dto.setId(1L);
        dto.setName("Latti");

        when(vendorService.getById(1L))
                .thenReturn(dto);

        mockMvc.perform(get("/vendors/get-by-id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Latti"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAll_shouldReturn200() throws Exception {

        VendorDTOResponse dto = new VendorDTOResponse();
        dto.setId(1L);
        dto.setName("Latti");

        List<VendorDTOResponse> dtoList = List.of(dto);

        when(vendorService.getAll())
                .thenReturn(dtoList);

        mockMvc.perform(get("/vendors/get-all-vendor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Latti"));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void save_shouldReturn200() throws Exception {

        VendorDTORequest dto = new VendorDTORequest();
        dto.setName("Latti");

        VendorDTOResponse vendorDTOResponse = new VendorDTOResponse();
        vendorDTOResponse.setId(1L);
        vendorDTOResponse.setName("Latti");

        when(vendorService.save(dto)).thenReturn(vendorDTOResponse);

        mockMvc.perform(
                        post("/vendors/save-vendor")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {
                            "name": "Latti"
                        }
                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Latti"));

        verify(vendorService).save(any(VendorDTORequest.class));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void softDelete_shouldReturn200OkAsStaff() throws Exception {
        doNothing().when(vendorService).softDelete(1L);

        mockMvc.perform(delete("/vendors/soft-delete-by-id/1").with(csrf()))
                .andExpect(status().isOk());

        verify(vendorService).softDelete(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void hardDelete_shouldReturn204NoContentAsAdmin() throws Exception {
        doNothing().when(vendorService).hardDelete(1L);

        mockMvc.perform(delete("/vendors/hard-delete-by-id/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(vendorService).hardDelete(1L);
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void hardDelete_shouldReturn403ForbiddenAsStaff() throws Exception {
        mockMvc.perform(delete("/vendors/hard-delete-by-id/1").with(csrf()))
                .andExpect(status().isForbidden());

        verify(vendorService, never()).hardDelete(anyLong());
    }
}
