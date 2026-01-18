package com.defiance.defiance.controller;

import com.defiance.defiance.dto.OrderItemCreateRequest;
import com.defiance.defiance.dto.OrderItemUpdateRequest;
import com.defiance.defiance.model.OrderItem;
import com.defiance.defiance.service.OrderItemService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {
    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping
    public ResponseEntity<OrderItem> create(@Valid @RequestBody OrderItemCreateRequest request) {
        OrderItem item = new OrderItem();
        item.setOrderId(request.getOrderId());
        item.setProductId(request.getProductId());
        item.setQty(request.getQty());
        item.setPrice(request.getPrice());
        item.setSubtotal(request.getSubtotal());
        OrderItem created = orderItemService.create(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<OrderItem>> getAll() {
        return ResponseEntity.ok(orderItemService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderItemService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItem> update(@PathVariable Long id, @Valid @RequestBody OrderItemUpdateRequest request) {
        OrderItem item = new OrderItem();
        item.setOrderId(request.getOrderId());
        item.setProductId(request.getProductId());
        item.setQty(request.getQty());
        item.setPrice(request.getPrice());
        item.setSubtotal(request.getSubtotal());
        return ResponseEntity.ok(orderItemService.update(id, item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
