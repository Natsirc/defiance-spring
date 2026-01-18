package com.defiance.defiance.service;

import com.defiance.defiance.exception.ResourceNotFoundException;
import com.defiance.defiance.model.CartItem;
import com.defiance.defiance.repository.CartItemRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CartItemService {
    private final CartItemRepository cartItemRepository;

    public CartItemService(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    public CartItem create(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }

    public List<CartItem> getAll() {
        return cartItemRepository.findAll();
    }

    public CartItem getById(Long id) {
        return cartItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
    }

    public CartItem update(Long id, CartItem updated) {
        CartItem existing = getById(id);
        updated.setId(existing.getId());
        return cartItemRepository.save(updated);
    }

    public void delete(Long id) {
        CartItem existing = getById(id);
        cartItemRepository.delete(existing);
    }
}
