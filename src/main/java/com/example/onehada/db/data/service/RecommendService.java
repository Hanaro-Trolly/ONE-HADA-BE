package com.example.onehada.db.data.service;
import com.example.onehada.db.data.ButtonNode;
import com.example.onehada.db.data.ProductNode;
import com.example.onehada.db.data.repository.ButtonNodeRepository;
import com.example.onehada.db.data.repository.ButtonRepository;
import com.example.onehada.db.data.repository.ProductNodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RecommendService {

    private final ButtonNodeRepository buttonNodeRepository;

    private final ProductNodeRepository productNodeRepository;
    private final ButtonRepository buttonRepository;

    // 생성자 주입
    public RecommendService(ButtonNodeRepository buttonNodeRepository, ProductNodeRepository productNodeRepository, ButtonRepository buttonRepository) {
        this.buttonNodeRepository = buttonNodeRepository;
        this.productNodeRepository = productNodeRepository;
        this.buttonRepository = buttonRepository;

    }

    public List<ProductNode> getRecommendProducts(String userId) {
        String buttonId = buttonRepository.findMostClickedButtonByUserId(userId);
        return productNodeRepository.findTop3RecommendedProductsByButton(buttonId);
    }
    @Transactional(readOnly = true)
    public String getMostClickedButton(String userId) {
        return buttonRepository.findMostClickedButtonByUserId(userId);
    }

    // @Transactional을 통해 트랜잭션 관리
    @Transactional("transactionManager = \"neo4jTransactionManager\"")
    public ProductNode createProduct(String name) {
        return productNodeRepository.save(new ProductNode(name));
    }
    @Transactional("transactionManager = \"neo4jTransactionManager\"")
    public ButtonNode createButton(String name) {
        return buttonNodeRepository.save(new ButtonNode(name));
    }

@Transactional("transactionManager = \"neo4jTransactionManager\"")
public void addRecommend(String buttonName, String productName) {
    Optional<ProductNode> product = productNodeRepository.findById(productName);
    Optional<ButtonNode> button = buttonNodeRepository.findById(buttonName);
    buttonNodeRepository.incrementRecommendationWeight(buttonName, productName,LocalDateTime.now());

    if (product.isPresent() && button.isPresent()) {
        // 버튼이 상품을 추천
        button.get().addRecommendedProduct(product.get());
        buttonNodeRepository.save(button.get());
    } else {
        if (product.isEmpty()) {
            throw new RuntimeException("Product not found: " + productName);
        }
        if (button.isEmpty()) {
            throw new RuntimeException("Button not found: " + buttonName);
        }
    }
}
    @Transactional(transactionManager = "neo4jTransactionManager",readOnly = true)
    public List<ProductNode> findAllProducts() {
        return productNodeRepository.findAll();
    }
    @Transactional(transactionManager = "neo4jTransactionManager",readOnly = true)
    public Set<ProductNode> findRecommends(String productName) {
        return productNodeRepository.findById(productName).map(ProductNode::getRecommendproduct).orElse(Collections.emptySet());
    }


}
