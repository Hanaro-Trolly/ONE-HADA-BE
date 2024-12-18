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

    @GetMapping//모든 상품
    public List<ProductNode> getAllProducts() {
        return recommendService.findAllProducts();
    }

    @PostMapping//상품 생성
    public ProductNode createProduct(@RequestBody ProductNode productNode) {
        return recommendService.createProduct(productNode.getName());
    }

    @PostMapping("/addReco") // 상품 추천 생성

    public String addRecommend(@RequestParam String buttonName,@RequestParam String ProductName) {
        recommendService.addRecommend(buttonName, ProductName );
        return buttonName + " recommend " + ProductName;
    }

    @GetMapping("/{name}/recommends") // 삭제 예정 상품 - 상품 추천
    public Set<ProductNode> getRecommend(@PathVariable String name) {
        return recommendService.findRecommends(name);
    }
    @GetMapping("/recommend/{buttonName}")//버튼에서 상품추천 상위 3개
    public List<ProductNode> getRecommendedProducts(@PathVariable String buttonName) {
        return recommendService.getTop3RecommendedProducts(buttonName);
    }
}
