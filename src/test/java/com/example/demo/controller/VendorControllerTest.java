package com.example.demo.controller;

import com.example.demo.dto.request.VendorDTORequest;
import com.example.demo.dto.response.VendorDTOResponse;
import com.example.demo.service.impl.VendorServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@WebMvcTest(VendorController.class)
public class VendorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VendorServiceImpl vendorService;

    @Test
    void getById_shouldReturn200() throws Exception {

        VendorDTOResponse dto = new VendorDTOResponse();
        dto.setId(1L);
        dto.setName("Latti");

        when(vendorService.getById(1L))
                .thenReturn(dto);

        mockMvc.perform(get("/vendors//get-by-id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Latti"));
    }


    @Test
    void getAll_shouldReturn200() throws Exception {

        // Arrange
        VendorDTOResponse dto = new VendorDTOResponse();
        dto.setId(1L);
        dto.setName("Latti");

        List<VendorDTOResponse> dtoList = List.of(dto);

        when(vendorService.getAll())
                .thenReturn(dtoList);

        // Act + Assert
        mockMvc.perform(get("/vendors/get-all-vendor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Latti"));
    }

        @Test
        void save_shouldReturn200() throws Exception {

            VendorDTORequest dto = new VendorDTORequest();
            dto.setName("Latti");

            VendorDTOResponse vendorDTOResponse = new VendorDTOResponse();
            vendorDTOResponse.setId(1L);
            vendorDTOResponse.setName("Latti");

            when(vendorService.save(dto)).thenReturn(vendorDTOResponse);

            mockMvc.perform(
                            post("/vendors/save-vendor")
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
    void delete_shouldDeleteVendorAndReturnNoContent() throws Exception{

            doNothing().when(vendorService).delete(1L);
        mockMvc.perform(delete("/vendors/delete-by-id/1"))
                .andExpect(status().isNoContent());
            verify(vendorService).delete(1L);
        }
}
