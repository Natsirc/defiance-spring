package com.defiance.defiance.controller;

import com.defiance.defiance.dto.CartItemCreateRequest;
import com.defiance.defiance.dto.CartItemUpdateRequest;
import com.defiance.defiance.model.CartItem;
import com.defiance.defiance.service.CartItemService;
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
@RequestMapping("/api/cart-items")
public class CartItemController {
    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @PostMapping
    public ResponseEntity<CartItem> create(@Valid @RequestBody CartItemCreateRequest request) {
        CartItem item = new CartItem();
        item.setCartId(request.getCartId());
        item.setProductId(request.getProductId());
        item.setQty(request.getQty());
        item.setPrice(request.getPrice());
        item.setSubtotal(request.getSubtotal());
        CartItem created = cartItemService.create(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getAll() {
        return ResponseEntity.ok(cartItemService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartItem> getById(@PathVariable Long id) {
        return ResponseEntity.ok(cartItemService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartItem> update(@PathVariable Long id, @Valid @RequestBody CartItemUpdateRequest request) {
        CartItem item = new CartItem();
        item.setCartId(request.getCartId());
        item.setProductId(request.getProductId());
        item.setQty(request.getQty());
        item.setPrice(request.getPrice());
        item.setSubtotal(request.getSubtotal());
        return ResponseEntity.ok(cartItemService.update(id, item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cartItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
