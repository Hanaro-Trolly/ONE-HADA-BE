package com.example.onehada.db.data.controller;

import com.example.onehada.db.data.ProductNode;
import com.example.onehada.db.data.service.ProductService;
import org.springframework.data.neo4j.repository.query.Query;
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

    public String addRecommend(@RequestParam String buttonName,@RequestParam String ProductName) {
        productService.addRecommend(buttonName, ProductName );
        return buttonName + " recommend " + ProductName;
    }

    @GetMapping("/{name}/recommends")
    public Set<ProductNode> getRecommend(@PathVariable String name) {
        return productService.findRecommends(name);
    }
    @GetMapping("/recommend/{buttonName}")
    public List<ProductNode> getRecommendedProducts(@PathVariable String buttonName) {
        return productService.getTop3RecommendedProducts(buttonName);
    }
}
