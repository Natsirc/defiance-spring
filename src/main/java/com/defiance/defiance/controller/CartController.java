package com.defiance.defiance.controller;

import com.defiance.defiance.dto.CartItemRequest;
import com.defiance.defiance.exception.ResourceNotFoundException;
import com.defiance.defiance.model.CartItem;
import com.defiance.defiance.model.Product;
import com.defiance.defiance.repository.CartItemRepository;
import com.defiance.defiance.repository.ProductRepository;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartController(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart(@RequestHeader(name = "X-Cart-Id", required = false) String cartId) {
        return ResponseEntity.ok(buildCartResponse(cartId));
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> add(
        @RequestHeader(name = "X-Cart-Id", required = false) String cartId,
        @Valid @RequestBody CartItemRequest request
    ) {
        ensureCartId(cartId);
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        CartItem item = cartItemRepository.findByCartIdAndProductId(cartId, product.getId())
            .orElseGet(CartItem::new);
        item.setCartId(cartId);
        item.setProductId(product.getId());
        int qty = request.getQty();
        item.setQty(qty);
        item.setPrice(product.getPrice());
        item.setSubtotal(product.getPrice() * qty);
        cartItemRepository.save(item);
        return ResponseEntity.ok(buildCartResponse(cartId));
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> update(
        @RequestHeader(name = "X-Cart-Id", required = false) String cartId,
        @Valid @RequestBody CartItemRequest request
    ) {
        ensureCartId(cartId);
        CartItem item = cartItemRepository.findByCartIdAndProductId(cartId, request.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        int qty = request.getQty();
        item.setQty(qty);
        item.setSubtotal(item.getPrice() * qty);
        cartItemRepository.save(item);
        return ResponseEntity.ok(buildCartResponse(cartId));
    }

    @PostMapping("/remove")
    public ResponseEntity<Map<String, Object>> remove(
        @RequestHeader(name = "X-Cart-Id", required = false) String cartId,
        @RequestBody Map<String, Object> payload
    ) {
        ensureCartId(cartId);
        Long productId = payload.get("product_id") == null ? null : Long.valueOf(payload.get("product_id").toString());
        if (productId != null) {
            cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .ifPresent(cartItemRepository::delete);
        }
        return ResponseEntity.ok(buildCartResponse(cartId));
    }

    @PostMapping("/clear")
    public ResponseEntity<Map<String, Object>> clear(@RequestHeader(name = "X-Cart-Id", required = false) String cartId) {
        ensureCartId(cartId);
        List<CartItem> items = cartItemRepository.findByCartId(cartId);
        cartItemRepository.deleteAll(items);
        return ResponseEntity.ok(buildCartResponse(cartId));
    }

    private Map<String, Object> buildCartResponse(String cartId) {
        ensureCartId(cartId);
        List<CartItem> items = cartItemRepository.findByCartId(cartId);
        int total = 0;
        List<Map<String, Object>> payload = new ArrayList<>();
        for (CartItem item : items) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            Map<String, Object> map = new HashMap<>();
            map.put("product_id", item.getProductId());
            map.put("qty", item.getQty());
            map.put("price", item.getPrice());
            map.put("subtotal", item.getSubtotal());
            if (product != null) {
                map.put("name", product.getName());
                map.put("image", product.getImage());
            }
            total += item.getSubtotal();
            payload.add(map);
        }
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("items", payload);
        body.put("total", total);
        return body;
    }

    private void ensureCartId(String cartId) {
        if (cartId == null || cartId.isBlank()) {
            throw new ResourceNotFoundException("Missing cart id");
        }
    }
}
