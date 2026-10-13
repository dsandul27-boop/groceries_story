package com.example.demo.service;

import com.example.demo.entities.Order;
import com.example.demo.entities.Status;
import com.example.demo.exception.EntityInUseException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private AddressRepository addressRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private OrderMapper orderMapper;
    @Mock private OrderItemRepository orderItemRepository;

    @InjectMocks private OrderServiceImpl orderService;

    private static final Long ORDER_ID = 1L;

    @Test
    void delete_orderNotFound_throwsResourceNotFound() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.delete(ORDER_ID));
        verify(orderRepository, never()).deleteById(any());
    }

    @Test
    void delete_hasOrderItems_throwsEntityInUse() {
        Order order = new Order();
        order.setStatus(Status.NEW);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderItemRepository.existsByOrderId(ORDER_ID)).thenReturn(true);

        assertThrows(EntityInUseException.class, () -> orderService.delete(ORDER_ID));
        verify(orderRepository, never()).deleteById(any());
    }

    @Test
    void delete_terminalStatus_throwsEntityInUse() {
        Order order = new Order();
        order.setStatus(Status.DELIVERED);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderItemRepository.existsByOrderId(ORDER_ID)).thenReturn(false);

        assertThrows(EntityInUseException.class, () -> orderService.delete(ORDER_ID));
        verify(orderRepository, never()).deleteById(any());
    }

    @Test
    void delete_cleanOrder_deletes() {
        Order order = new Order();
        order.setStatus(Status.NEW);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderItemRepository.existsByOrderId(ORDER_ID)).thenReturn(false);

        orderService.delete(ORDER_ID);

        verify(orderRepository).deleteById(ORDER_ID);
    }
}