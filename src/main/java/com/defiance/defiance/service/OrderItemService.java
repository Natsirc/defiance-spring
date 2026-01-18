package com.defiance.defiance.service;

import com.defiance.defiance.exception.ResourceNotFoundException;
import com.defiance.defiance.model.OrderItem;
import com.defiance.defiance.repository.OrderItemRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public OrderItem create(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    public List<OrderItem> getAll() {
        return orderItemRepository.findAll();
    }

    public OrderItem getById(Long id) {
        return orderItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));
    }

    public OrderItem update(Long id, OrderItem updated) {
        OrderItem existing = getById(id);
        updated.setId(existing.getId());
        return orderItemRepository.save(updated);
    }

    public void delete(Long id) {
        OrderItem existing = getById(id);
        orderItemRepository.delete(existing);
    }
}
