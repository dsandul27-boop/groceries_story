package com.example.demo.controller;

import com.example.demo.dto.request.VendorDTORequest;
import com.example.demo.dto.response.VendorDTOResponse;
import com.example.demo.entities.Vendor;
import com.example.demo.service.impl.VendorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendors")
public class VendorController {

    private final VendorServiceImpl vendorService;

    @Autowired
    public VendorController(VendorServiceImpl vendorService) {
        this.vendorService = vendorService;
    }

    @GetMapping
    public List<VendorDTOResponse> getAll(){
        return vendorService.getAll();
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<VendorDTOResponse> getById(@PathVariable Long id){
         VendorDTOResponse vendorDTOResponse = vendorService.getById(id);
        return new ResponseEntity<>(vendorDTOResponse, HttpStatus.ACCEPTED);
    }

    @PostMapping("/save-vendor")
    public Long save(@RequestBody VendorDTORequest vendorDTORequest){
        return vendorService.save(vendorDTORequest);
    }

    @DeleteMapping("/delete-by-id/{id}")
    public void delete(@PathVariable Long id){
        vendorService.delete(id);
    }



}
