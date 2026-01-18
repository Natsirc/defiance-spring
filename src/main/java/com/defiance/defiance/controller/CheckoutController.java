package com.defiance.defiance.controller;

import com.defiance.defiance.dto.CheckoutRequest;
import com.defiance.defiance.exception.ResourceNotFoundException;
import com.defiance.defiance.model.CartItem;
import com.defiance.defiance.model.Order;
import com.defiance.defiance.model.OrderItem;
import com.defiance.defiance.model.Product;
import com.defiance.defiance.model.User;
import com.defiance.defiance.repository.CartItemRepository;
import com.defiance.defiance.repository.OrderItemRepository;
import com.defiance.defiance.repository.OrderRepository;
import com.defiance.defiance.repository.ProductRepository;
import com.defiance.defiance.service.AuthService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CheckoutController {
    private static final int SHIPPING_FEE = 100;

    private final AuthService authService;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public CheckoutController(
        AuthService authService,
        CartItemRepository cartItemRepository,
        ProductRepository productRepository,
        OrderRepository orderRepository,
        OrderItemRepository orderItemRepository
    ) {
        this.authService = authService;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(
        @RequestHeader(name = "Authorization", required = false) String authHeader,
        @RequestHeader(name = "X-Cart-Id", required = false) String cartId,
        @Valid @RequestBody CheckoutRequest request
    ) {
        User user = authService.requireUser(authHeader);
        if (cartId == null || cartId.isBlank()) {
            throw new ResourceNotFoundException("Missing cart id");
        }
        List<CartItem> items = cartItemRepository.findByCartId(cartId);
        if (items.isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty");
        }
        int subtotal = items.stream().mapToInt(CartItem::getSubtotal).sum();
        int total = subtotal + SHIPPING_FEE;

        Order order = new Order();
        order.setUserId(user.getId());
        order.setFullName(request.getFullName());
        order.setEmail(user.getEmail());
        order.setPhone(request.getPhone());
        order.setAddressLine(request.getAddressLine());
        order.setBarangay(request.getBarangay());
        order.setCity(request.getCity());
        order.setProvince(request.getProvince());
        order.setPostalCode(request.getPostalCode());
        order.setPaymentMethod(request.getPaymentMethod() == null ? "gcash" : request.getPaymentMethod());
        order.setReceiptPath(null);
        order.setTotal(total);
        order.setStatus("pending_payment_confirmation");
        order.setCreatedAt(LocalDateTime.now());
        Order saved = orderRepository.save(order);

        for (CartItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(saved.getId());
            orderItem.setProductId(product.getId());
            orderItem.setQty(item.getQty());
            orderItem.setPrice(item.getPrice());
            orderItem.setSubtotal(item.getSubtotal());
            orderItemRepository.save(orderItem);
        }

        cartItemRepository.deleteAll(items);

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("order_id", saved.getId());
        body.put("total", total);
        return ResponseEntity.ok(body);
    }
}
