package com.example.onehada.db.data.controller;

import com.example.onehada.auth.service.JwtService;
import com.example.onehada.db.data.ProductNode;
import com.example.onehada.db.data.service.RecommendService;
import com.example.onehada.db.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/product")
public class ProductNodeController {

    private final RecommendService recommendService;

    private final JwtService jwtService;

    public ProductNodeController(RecommendService recommendService, JwtService jwtService) {
        this.recommendService = recommendService;
        this.jwtService = jwtService;
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

    @GetMapping("/recommend")
    public ResponseEntity<ApiResponse> getRecommendedProducts(@RequestHeader("Authorization") String token) {
        String accessToken = token.replace("Bearer ", "");
        String userId = jwtService.extractUserId(accessToken).toString();
        return ResponseEntity.ok(new ApiResponse(200, "OK", "추천 상품 조회 성공", recommendService.getRecommendProducts(userId)));
    }
}
