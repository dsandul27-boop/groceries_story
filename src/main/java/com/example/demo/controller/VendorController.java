package com.example.demo.controller;

import com.example.demo.dto.request.VendorDTORequest;
import com.example.demo.dto.response.VendorDTOResponse;
import com.example.demo.entities.Vendor;
import com.example.demo.service.impl.VendorServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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

    @GetMapping("/get-all-vendor")
    public ResponseEntity<List<VendorDTOResponse>> getAll(){
        return new ResponseEntity<>(vendorService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<VendorDTOResponse> getById(@PathVariable @Positive(message = "ID must be positive") Long id){
         VendorDTOResponse vendorDTOResponse = vendorService.getById(id);
        return new ResponseEntity<>(vendorDTOResponse, HttpStatus.OK);
    }

    @PostMapping("/save-vendor")
    public ResponseEntity<VendorDTOResponse> save(@Valid @RequestBody VendorDTORequest vendorDTORequest){
        return new ResponseEntity<>(vendorService.save(vendorDTORequest), HttpStatus.OK);
    }

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity delete(@Valid @PathVariable @Positive(message = "Must be positive") Long id){
        vendorService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



}
