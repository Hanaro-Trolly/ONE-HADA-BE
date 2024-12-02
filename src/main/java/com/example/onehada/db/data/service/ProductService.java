package com.example.onehada.db.data.service;
import com.example.onehada.db.data.ButtonNode;
import com.example.onehada.db.data.ProductNode;
import com.example.onehada.db.data.repository.ButtonNodeRepository;
import com.example.onehada.db.data.repository.ProductNodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProductService {

    private final ButtonNodeRepository buttonNodeRepository;

    private final ProductNodeRepository productNodeRepository;

    // 생성자 주입
    public ProductService(ButtonNodeRepository buttonNodeRepository, ProductNodeRepository productNodeRepository) {
        this.buttonNodeRepository = buttonNodeRepository;
        this.productNodeRepository = productNodeRepository;
    }

    // @Transactional을 통해 트랜잭션 관리
    @Transactional
    public ProductNode createProduct(String name) {
        return productNodeRepository.save(new ProductNode(name));
    }
    @Transactional
    public ButtonNode createButton(String name) {
        return buttonNodeRepository.save(new ButtonNode(name));
    }

    @Transactional
    public void addRecommend(String buttonName, String productName) {
        Optional<ProductNode> product = productNodeRepository.findById(productName);
        Optional<ButtonNode> button = buttonNodeRepository.findById(buttonName);

        if (product.isPresent() && button.isPresent()) {
            button.get().getRecommendedProduct().add(button.get());
            product.get().getRecommendproduct().add(product.get());
            buttonNodeRepository.save(button.get());
            productNodeRepository.save(product.get());
        } else {
            if (product.isEmpty()) {
                throw new RuntimeException("Product not found: " + productName);
            }
            if (button.isEmpty()) {
                throw new RuntimeException("Button not found: " + buttonName);
            }
        }
    }
    @Transactional(readOnly = true)
    public List<ProductNode> findAllProducts() {
        return productNodeRepository.findAll();
    }
    @Transactional(readOnly = true)
    public Set<ProductNode> findRecommends(String productName) {
        return productNodeRepository.findById(productName).map(ProductNode::getRecommendproduct).orElse(Set.of());
    }


}
