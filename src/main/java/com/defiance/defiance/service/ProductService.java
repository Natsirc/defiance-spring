package com.defiance.defiance.service;

import com.defiance.defiance.exception.ResourceNotFoundException;
import com.defiance.defiance.model.Product;
import com.defiance.defiance.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public Product update(Long id, Product updated) {
        Product existing = getById(id);
        updated.setId(existing.getId());
        return productRepository.save(updated);
    }

    public void delete(Long id) {
        Product existing = getById(id);
        productRepository.delete(existing);
    }
}
