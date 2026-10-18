package com.example.demo.service;

import com.example.demo.dto.request.OrderDTORequest;
import com.example.demo.dto.response.OrderDTOResponse;

import java.util.List;


public interface OrderService {

    OrderDTOResponse save(OrderDTORequest orderDTORequest);
    List<OrderDTOResponse> getAll();
    OrderDTOResponse getById(Long orderId);
    void delete(Long orderId);
}
