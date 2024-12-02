package com.example.onehada.db.data.controller;

import com.example.onehada.db.data.ProductNode;
import com.example.onehada.db.data.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/product")
public class ProductNodeController {

    private final ProductService productService;

    public ProductNodeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductNode> getAllProducts() {
        return productService.findAllProducts();
    }

    @PostMapping
    public ProductNode createProduct(@RequestBody ProductNode productNode) {
        return productService.createProduct(productNode.getName());
    }
    @PostMapping("/addReco")
    public String addRecommend(@RequestParam String ProductName, @RequestParam String buttonName) {
        productService.addRecommend(ProductName, buttonName);
        return ProductName + " is recommendation about " + buttonName;
    }

    @GetMapping("/{name}/recommends")
    public Set<ProductNode> getRecommend(@PathVariable String name) {
        return productService.findRecommends(name);
    }
}
