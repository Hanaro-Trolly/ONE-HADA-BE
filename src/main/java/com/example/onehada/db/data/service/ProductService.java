package com.example.onehada.db.data.service;
import com.example.onehada.db.data.ProductNode;
import com.example.onehada.db.data.repository.ProductNodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProductService {

    private final ProductNodeRepository productNodeRepository;

    // 생성자 주입
    public ProductService(ProductNodeRepository productNodeRepository) {
        this.productNodeRepository = productNodeRepository;
    }

    // @Transactional을 통해 트랜잭션 관리
    @Transactional
    public ProductNode createProduct(String name) {
        return productNodeRepository.save(new ProductNode(name));
    }

    @Transactional
    public void addRecommend(String productName, String recommendName) {
        Optional<ProductNode> product = productNodeRepository.findById(productName);
        Optional<ProductNode> recommend = productNodeRepository.findById(recommendName);

        if (product.isPresent() && recommend.isPresent()) {
            product.get().getRecommendproduct().add(recommend.get());
            productNodeRepository.save(product.get());
        } else {
            throw new RuntimeException("Product or Recoomend not found!");
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
