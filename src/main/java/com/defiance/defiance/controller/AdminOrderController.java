package com.defiance.defiance.controller;

import com.defiance.defiance.dto.StatusUpdateRequest;
import com.defiance.defiance.exception.ResourceNotFoundException;
import com.defiance.defiance.model.Order;
import com.defiance.defiance.model.OrderItem;
import com.defiance.defiance.model.Product;
import com.defiance.defiance.repository.OrderItemRepository;
import com.defiance.defiance.repository.OrderRepository;
import com.defiance.defiance.repository.ProductRepository;
import com.defiance.defiance.service.AuthService;
import com.defiance.defiance.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public AdminOrderController(
        AuthService authService,
        OrderRepository orderRepository,
        OrderItemRepository orderItemRepository,
        ProductRepository productRepository
    ) {
        this.authService = authService;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
        @RequestHeader(name = "Authorization", required = false) String authHeader,
        HttpServletRequest request
    ) {
        authService.requireAdmin(authHeader);
        String baseUrl = baseUrl(request);
        List<Order> orders = orderRepository.findAll();
        List<Map<String, Object>> payload = new ArrayList<>();
        for (Order order : orders) {
            payload.add(ResponseUtil.orderToMap(order, null, baseUrl));
        }
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("orders", payload);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> detail(
        @RequestHeader(name = "Authorization", required = false) String authHeader,
        @PathVariable Long id,
        HttpServletRequest request
    ) {
        authService.requireAdmin(authHeader);
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        String baseUrl = baseUrl(request);
        Map<String, Object> mapped = ResponseUtil.orderToMap(order, null, baseUrl);
        List<Map<String, Object>> itemMaps = new ArrayList<>();
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            Map<String, Object> im = new HashMap<>();
            im.put("product_id", item.getProductId());
            im.put("qty", item.getQty());
            im.put("price", item.getPrice());
            im.put("subtotal", item.getSubtotal());
            if (product != null) {
                im.put("name", product.getName());
                im.put("image", product.getImage());
            }
            itemMaps.add(im);
        }
        mapped.put("items", itemMaps);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("order", mapped);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
        @RequestHeader(name = "Authorization", required = false) String authHeader,
        @PathVariable Long id,
        @Valid @RequestBody StatusUpdateRequest request
    ) {
        authService.requireAdmin(authHeader);
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(request.getStatus());
        orderRepository.save(order);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(
        @RequestHeader(name = "Authorization", required = false) String authHeader,
        @PathVariable Long id
    ) {
        authService.requireAdmin(authHeader);
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        orderRepository.delete(order);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        return ResponseEntity.ok(body);
    }

    private String baseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }
}
