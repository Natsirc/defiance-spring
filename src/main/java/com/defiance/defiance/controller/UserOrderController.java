package com.defiance.defiance.controller;

import com.defiance.defiance.exception.ResourceNotFoundException;
import com.defiance.defiance.exception.UnauthorizedException;
import com.defiance.defiance.model.Order;
import com.defiance.defiance.model.OrderItem;
import com.defiance.defiance.model.Product;
import com.defiance.defiance.model.User;
import com.defiance.defiance.repository.OrderItemRepository;
import com.defiance.defiance.repository.OrderRepository;
import com.defiance.defiance.repository.ProductRepository;
import com.defiance.defiance.service.AuthService;
import com.defiance.defiance.util.ResponseUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth/orders")
public class UserOrderController {
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public UserOrderController(
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
        User user = authService.requireUser(authHeader);
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        String baseUrl = baseUrl(request);
        List<Map<String, Object>> payload = new ArrayList<>();
        for (Order order : orders) {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
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
            payload.add(mapped);
        }
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("orders", payload);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/{id}/receipt")
    public ResponseEntity<Map<String, Object>> uploadReceipt(
        @RequestHeader(name = "Authorization", required = false) String authHeader,
        @PathVariable Long id,
        @RequestPart("receipt") MultipartFile receipt,
        HttpServletRequest request
    ) throws IOException {
        User user = authService.requireUser(authHeader);
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("Unauthorized");
        }
        if (receipt == null || receipt.isEmpty()) {
            throw new ResourceNotFoundException("Receipt is required");
        }
        Path uploadDir = Paths.get("uploads", "receipts");
        Files.createDirectories(uploadDir);
        String fileName = System.currentTimeMillis() + "_" + receipt.getOriginalFilename();
        Path filePath = uploadDir.resolve(fileName);
        Files.write(filePath, receipt.getBytes());
        order.setReceiptPath("/uploads/receipts/" + fileName);
        orderRepository.save(order);

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancel(
        @RequestHeader(name = "Authorization", required = false) String authHeader,
        @PathVariable Long id
    ) {
        User user = authService.requireUser(authHeader);
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("Unauthorized");
        }
        if (!"pending_payment_confirmation".equalsIgnoreCase(order.getStatus())) {
            throw new UnauthorizedException("Order cannot be cancelled");
        }
        order.setStatus("cancelled");
        orderRepository.save(order);

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        return ResponseEntity.ok(body);
    }

    private String baseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }
}
