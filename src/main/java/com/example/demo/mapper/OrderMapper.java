package com.example.demo.mapper;

import com.example.demo.dto.response.OrderDTOResponse;
import com.example.demo.entities.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderDTOResponse toDTO(Order order){
        OrderDTOResponse orderDTOResponse = new OrderDTOResponse();
        orderDTOResponse.setId(order.getId());
        return  orderDTOResponse;
    }
}
