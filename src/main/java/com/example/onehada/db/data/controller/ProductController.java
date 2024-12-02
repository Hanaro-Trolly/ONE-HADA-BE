package com.example.onehada.db.data.controller;

import com.example.onehada.db.data.Product;
import com.example.onehada.db.data.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // 모든 사용자 조회
    @GetMapping
    public List<Product> getAllUsers() {
        return productRepository.findAll();
    }

    // 새 사용자 추가
    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }
}

