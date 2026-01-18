package com.defiance.defiance.controller;

import com.defiance.defiance.dto.OrderCreateRequest;
import com.defiance.defiance.dto.OrderUpdateRequest;
import com.defiance.defiance.model.Order;
import com.defiance.defiance.service.OrderService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
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
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> create(@Valid @RequestBody OrderCreateRequest request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setFullName(request.getFullName());
        order.setEmail(request.getEmail());
        order.setPhone(request.getPhone());
        order.setAddressLine(request.getAddressLine());
        order.setBarangay(request.getBarangay());
        order.setCity(request.getCity());
        order.setProvince(request.getProvince());
        order.setPostalCode(request.getPostalCode());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setReceiptPath(request.getReceiptPath());
        order.setTotal(request.getTotal());
        order.setStatus(request.getStatus());
        order.setCreatedAt(LocalDateTime.now());
        Order created = orderService.create(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> update(@PathVariable Long id, @Valid @RequestBody OrderUpdateRequest request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setFullName(request.getFullName());
        order.setEmail(request.getEmail());
        order.setPhone(request.getPhone());
        order.setAddressLine(request.getAddressLine());
        order.setBarangay(request.getBarangay());
        order.setCity(request.getCity());
        order.setProvince(request.getProvince());
        order.setPostalCode(request.getPostalCode());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setReceiptPath(request.getReceiptPath());
        order.setTotal(request.getTotal());
        order.setStatus(request.getStatus());
        Order existing = orderService.getById(id);
        order.setCreatedAt(existing.getCreatedAt());
        return ResponseEntity.ok(orderService.update(id, order));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
