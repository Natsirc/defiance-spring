package com.defiance.defiance.service;

import com.defiance.defiance.exception.ResourceNotFoundException;
import com.defiance.defiance.model.Order;
import com.defiance.defiance.repository.OrderRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order create(Order order) {
        return orderRepository.save(order);
    }

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public Order getById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    public Order update(Long id, Order updated) {
        Order existing = getById(id);
        updated.setId(existing.getId());
        return orderRepository.save(updated);
    }

    public void delete(Long id) {
        Order existing = getById(id);
        orderRepository.delete(existing);
    }
}
