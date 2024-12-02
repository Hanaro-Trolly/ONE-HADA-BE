package com.example.onehada.db.data.service;

import com.example.onehada.db.data.Button;
import com.example.onehada.db.data.Product;
import com.example.onehada.db.data.ButtonNode;
import com.example.onehada.db.data.ProductNode;
import com.example.onehada.db.data.repository.ButtonRepository;
import com.example.onehada.db.data.repository.ButtonNodeRepository;
import com.example.onehada.db.data.repository.ProductRepository;
import com.example.onehada.db.data.repository.ProductNodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DataTransferService {

    private final ProductRepository productMongoRepository;
    private final ButtonRepository buttonMongoRepository;
    private final ProductNodeRepository productNeo4jRepository;
    private final ButtonNodeRepository buttonNeo4jRepository;

    public DataTransferService(ProductRepository productMongoRepository,
                               ButtonRepository buttonMongoRepository,
                               ProductNodeRepository productNeo4jRepository,
                               ButtonNodeRepository buttonNeo4jRepository) {
        this.productMongoRepository = productMongoRepository;
        this.buttonMongoRepository = buttonMongoRepository;
        this.productNeo4jRepository = productNeo4jRepository;
        this.buttonNeo4jRepository = buttonNeo4jRepository;
    }

    @Transactional
    public void transferDataToNeo4j() {
        // Product 데이터 전송
        List<Product> mongoProducts = productMongoRepository.findAll();
        mongoProducts.forEach(product -> {
            ProductNode productNode = new ProductNode();
            productNode.setName(product.getName());
            productNeo4jRepository.save(productNode);
        });

        // Button 데이터 전송
        List<Button> mongoButtons = buttonMongoRepository.findAll();
        mongoButtons.forEach(button -> {
            ButtonNode buttonNode = new ButtonNode();
            buttonNode.setName(button.getName());

            buttonNeo4jRepository.save(buttonNode);
        });
    }
}
