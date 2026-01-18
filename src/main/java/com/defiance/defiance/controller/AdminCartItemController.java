package com.defiance.defiance.controller;

import com.defiance.defiance.model.CartItem;
import com.defiance.defiance.repository.CartItemRepository;
import com.defiance.defiance.service.AuthService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/cart-items")
public class AdminCartItemController {
    private final CartItemRepository cartItemRepository;
    private final AuthService authService;

    public AdminCartItemController(CartItemRepository cartItemRepository, AuthService authService) {
        this.cartItemRepository = cartItemRepository;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        authService.requireAdmin(authHeader);
        List<Map<String, Object>> items = cartItemRepository.findAll().stream()
            .map(item -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", item.getId());
                map.put("cart_id", item.getCartId());
                map.put("product_id", item.getProductId());
                map.put("qty", item.getQty());
                map.put("price", item.getPrice());
                map.put("subtotal", item.getSubtotal());
                return map;
            })
            .collect(Collectors.toList());
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("items", items);
        return ResponseEntity.ok(body);
    }
}
