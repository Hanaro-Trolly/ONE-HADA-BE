package com.example.onehada.db.data.controller;

import com.example.onehada.db.data.ProductNode;
import com.example.onehada.db.data.service.RecommendService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/product")
public class ProductNodeController {

    private final RecommendService recommendService;

    public ProductNodeController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }

    @GetMapping
    public List<ProductNode> getAllProducts() {
        return recommendService.findAllProducts();
    }

    @PostMapping
    public ProductNode createProduct(@RequestBody ProductNode productNode) {
        return recommendService.createProduct(productNode.getName());
    }

    @PostMapping("/addReco")

    public String addRecommend(@RequestParam String buttonName,@RequestParam String ProductName) {
        recommendService.addRecommend(buttonName, ProductName );
        return buttonName + " recommend " + ProductName;
    }

    @GetMapping("/{name}/recommends")
    public Set<ProductNode> getRecommend(@PathVariable String name) {
        return recommendService.findRecommends(name);
    }
    @GetMapping("/recommend/{buttonName}")
    public List<ProductNode> getRecommendedProducts(@PathVariable String buttonName) {
        return recommendService.getTop3RecommendedProducts(buttonName);
    }
}
