package com.example.demo.mapper;

import com.example.demo.dto.request.AddressDTORequest;
import com.example.demo.dto.response.AddressResponse;
import com.example.demo.entities.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public AddressResponse toDTO(Address address){
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setCity(address.getCity());
        response.setApartment(address.getApartment());
        response.setStreet(address.getStreet());
        response.setPostalCode(address.getPostalCode());
        response.setHouseNumber(address.getHouseNumber());
        response.setUserId(address.getId());
        return response;
    }

    public Address toEntity(AddressDTORequest request){
        Address address = new Address();
        address.setApartment(request.getApartment());
        address.setCity(request.getCity());
        address.setStreet(address.getStreet());
        address.setPostalCode(request.getPostalCode());
        address.setHouseNumber(request.getHouseNumber());
        return address;
    }
}
