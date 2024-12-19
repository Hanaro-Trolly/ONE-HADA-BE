package com.example.onehada.db.data.controller;

import com.example.onehada.db.data.ProductNode;
import com.example.onehada.db.data.service.RecommendService;
import com.example.onehada.db.dto.ApiResult;
import com.example.onehada.auth.service.JwtService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Product", description = "상품 및 추천 관련 API")
@RequestMapping("/api/product")
public class ProductNodeController {

    private final RecommendService recommendService;
    private final JwtService jwtService;

    public ProductNodeController(RecommendService recommendService, JwtService jwtService) {
        this.recommendService = recommendService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "전체 상품 조회", description = "모든 상품 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping//모든 상품
    public List<ProductNode> getAllProducts() {
        return recommendService.findAllProducts();
    }

    @Operation(summary = "상품 생성", description = "새로운 상품을 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping//상품 생성
    public ProductNode createProduct(@RequestBody ProductNode productNode) {
        return recommendService.createProduct(productNode.getName());
    }

    @Operation(summary = "추천 관계 생성", description = "버튼과 상품 간의 추천 관계를 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/addReco") // 상품 추천 생성
    public String addRecommend(@RequestParam String buttonName,@RequestParam String ProductName) {
        recommendService.addRecommend(buttonName, ProductName );
        return buttonName + " recommend " + ProductName;
    }

    @Operation(summary = "상품 추천 조회", description = "특정 상품에 대한 추천 상품 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    @GetMapping("/{name}/recommends") // 삭제 예정 상품 - 상품 추천
    public Set<ProductNode> getRecommend(@PathVariable String name) {
        return recommendService.findRecommends(name);
    }

    @Operation(summary = "추천 상품 조회", description = "사용자의 활동 기반으로 추천 상품을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/recommend")
    public ResponseEntity<ApiResult> getRecommendedProducts(@RequestHeader("Authorization") String token) {
        String accessToken = token.replace("Bearer ", "");
        String userId = jwtService.extractUserId(accessToken).toString();
        return ResponseEntity.ok(new ApiResult(200, "OK", "추천 상품 조회 성공", recommendService.getRecommendProducts(userId)));
    }
}
