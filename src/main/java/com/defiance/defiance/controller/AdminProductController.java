package com.defiance.defiance.controller;

import com.defiance.defiance.exception.ResourceNotFoundException;
import com.defiance.defiance.model.Product;
import com.defiance.defiance.repository.ProductRepository;
import com.defiance.defiance.service.AuthService;
import com.defiance.defiance.util.ResponseUtil;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {
    private final ProductRepository productRepository;
    private final AuthService authService;

    public AdminProductController(ProductRepository productRepository, AuthService authService) {
        this.productRepository = productRepository;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        authService.requireAdmin(authHeader);
        List<Map<String, Object>> products = productRepository.findAll().stream()
            .map(ResponseUtil::productToMap)
            .collect(Collectors.toList());
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("products", products);
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
        @RequestHeader(name = "Authorization", required = false) String authHeader,
        @Valid @RequestBody Map<String, Object> payload
    ) {
        authService.requireAdmin(authHeader);
        Product product = new Product();
        product.setName(payload.get("name").toString());
        product.setPrice(Integer.parseInt(payload.get("price").toString()));
        product.setCategory(payload.get("category") == null ? null : payload.get("category").toString());
        product.setIsActive(Boolean.parseBoolean(payload.get("is_active").toString()));
        product.setStock(Integer.parseInt(payload.get("stock").toString()));
        product.setImage(null);
        Product saved = productRepository.save(product);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("product", ResponseUtil.productToMap(saved));
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(
        @RequestHeader(name = "Authorization", required = false) String authHeader,
        @PathVariable Long id,
        @RequestBody Map<String, Object> payload
    ) {
        authService.requireAdmin(authHeader);
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (payload.containsKey("name")) product.setName(payload.get("name").toString());
        if (payload.containsKey("price")) product.setPrice(Integer.parseInt(payload.get("price").toString()));
        if (payload.containsKey("category")) product.setCategory(payload.get("category").toString());
        if (payload.containsKey("is_active")) product.setIsActive(Boolean.parseBoolean(payload.get("is_active").toString()));
        if (payload.containsKey("stock")) product.setStock(Integer.parseInt(payload.get("stock").toString()));
        Product saved = productRepository.save(product);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("product", ResponseUtil.productToMap(saved));
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(
        @RequestHeader(name = "Authorization", required = false) String authHeader,
        @PathVariable Long id
    ) {
        authService.requireAdmin(authHeader);
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productRepository.delete(product);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<Map<String, Object>> uploadImage(
        @RequestHeader(name = "Authorization", required = false) String authHeader,
        @PathVariable Long id,
        @RequestPart("image") MultipartFile image
    ) throws IOException {
        authService.requireAdmin(authHeader);
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (image == null || image.isEmpty()) {
            throw new ResourceNotFoundException("Image is required");
        }
        Path uploadDir = Paths.get("uploads", "products");
        Files.createDirectories(uploadDir);
        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path filePath = uploadDir.resolve(fileName);
        Files.write(filePath, image.getBytes());
        product.setImage("/uploads/products/" + fileName);
        productRepository.save(product);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("product", ResponseUtil.productToMap(product));
        return ResponseEntity.ok(body);
    }
}
