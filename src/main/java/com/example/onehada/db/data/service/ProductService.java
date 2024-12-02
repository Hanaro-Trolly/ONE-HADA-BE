package com.example.onehada.db.data.service;
import com.example.onehada.db.data.ProductNode;
import com.example.onehada.db.data.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    // 생성자 주입
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // @Transactional을 통해 트랜잭션 관리
    @Transactional
    public ProductNode createProduct(String name) {
        return productRepository.save(new ProductNode(name));
    }

    @Transactional
    public void addRecommend(String productName, String recommendName) {
        Optional<ProductNode> product = productRepository.findById(productName);
        Optional<ProductNode> recommend = productRepository.findById(recommendName);

        if (product.isPresent() && recommend.isPresent()) {
            product.get().getRecommendproduct().add(recommend.get());
            productRepository.save(product.get());
        } else {
            throw new RuntimeException("Product or Recoomend not found!");
        }
    }
    @Transactional(readOnly = true)
    public List<ProductNode> findAllProducts() {
        return productRepository.findAll();
    }
    @Transactional(readOnly = true)
    public Set<ProductNode> findRecommends(String productName) {
        return productRepository.findById(productName).map(ProductNode::getRecommendproduct).orElse(Set.of());
    }


}
