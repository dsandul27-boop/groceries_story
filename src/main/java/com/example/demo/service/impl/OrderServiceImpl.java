package com.example.demo.service.impl;

import com.example.demo.dto.request.OrderDTORequest;
import com.example.demo.dto.response.OrderDTOResponse;
import com.example.demo.entities.*;
import com.example.demo.exception.EntityInUseException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderServiceImpl(UserRepository userRepository, AddressRepository addressRepository, OrderRepository orderRepository, OrderMapper orderMapper, OrderItemRepository orderItemRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public OrderDTOResponse save(OrderDTORequest orderDTORequest) {
        Optional<Address> optionalAddress = addressRepository.findById(orderDTORequest.getAddressId());
        if (optionalAddress.isEmpty()) {
            throw new ResourceNotFoundException("Address is empty");
        }
        Order order = new Order();
        Optional<User> optionalUser = (userRepository.findById(orderDTORequest.getUserId()));
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        order.setAddress(optionalAddress.get());
        order.setUser(optionalUser.get());
        order.setStatus(Status.NEW);
        order.setCreatedAt(Instant.now());
        order.setTotalPrice(orderDTORequest.getTotalPrice());
        return orderMapper.toDTO(orderRepository.save(order));
    }

    @Override
    public List<OrderDTOResponse> getAll() {
        List<OrderDTOResponse> orderDTOResponseList = new ArrayList<>();
        List<Order> orderList = orderRepository.findAll();
        if (orderList.isEmpty()) {
            throw new ResourceNotFoundException("No one order found");
        }
        for (Order order : orderList) {
            orderDTOResponseList.add(orderMapper.toDTO(order));
        }
        return orderDTOResponseList;
    }

    @Override
    public OrderDTOResponse getById(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            throw new ResourceNotFoundException("Order was not found");
        }
        return orderMapper.toDTO(optionalOrder.get());
    }

    @Override
    public void delete(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            throw new ResourceNotFoundException("Order doesn't exist");
        }
        if (orderItemRepository.existsById(orderId)) {
            throw new EntityInUseException("Exist order item dependencies");
        }
        if (optionalOrder.get().getStatus().equals(Status.DELIVERED) || optionalOrder.get().getStatus().equals(Status.CANCELLED)) {
            throw new EntityInUseException("Order is not in terminated status");
        }
        orderRepository.deleteById(orderId);
    }
}
