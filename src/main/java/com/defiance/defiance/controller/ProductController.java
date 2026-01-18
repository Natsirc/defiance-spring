package com.defiance.defiance.controller;

import com.defiance.defiance.dto.ProductCreateRequest;
import com.defiance.defiance.dto.ProductUpdateRequest;
import com.defiance.defiance.model.Product;
import com.defiance.defiance.service.ProductService;
import com.defiance.defiance.util.ResponseUtil;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody ProductCreateRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setImage(request.getImage());
        product.setCategory(request.getCategory());
        product.setIsActive(request.getIsActive());
        product.setStock(request.getStock());
        Product created = productService.create(product);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("product", ResponseUtil.productToMap(created));
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(@RequestParam(name = "all", required = false) Boolean all) {
        List<Product> source = Boolean.TRUE.equals(all)
            ? productService.getAll()
            : productService.getAll().stream().filter(p -> Boolean.TRUE.equals(p.getIsActive())).toList();
        List<Map<String, Object>> products = source.stream()
            .map(ResponseUtil::productToMap)
            .collect(Collectors.toList());
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("products", products);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Product product = productService.getById(id);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("product", ResponseUtil.productToMap(product));
        return ResponseEntity.ok(body);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setImage(request.getImage());
        product.setCategory(request.getCategory());
        product.setIsActive(request.getIsActive());
        product.setStock(request.getStock());
        Product updated = productService.update(id, product);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("product", ResponseUtil.productToMap(updated));
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        productService.delete(id);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        return ResponseEntity.ok(body);
    }
}
