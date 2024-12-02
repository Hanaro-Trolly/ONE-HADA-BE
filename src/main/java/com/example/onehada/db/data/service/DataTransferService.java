package com.example.onehada.db.data.service;

import com.example.onehada.db.data.Button;
import com.example.onehada.db.data.repository.ButtonRepository;
import com.example.onehada.db.data.ProductNode;
import com.example.onehada.db.data.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class DataTransferService {

    private final ButtonRepository ButtonMongoRepository;
    private final ProductRepository productNeo4jRepository;

    public DataTransferService(ButtonRepository ButtonMongoRepository, ProductRepository productNeo4jRepository) {
        this.ButtonMongoRepository = ButtonMongoRepository;
        this.productNeo4jRepository = productNeo4jRepository;
    }

    @Transactional
    public void transferDataToNeo4j() {
        List<Button> mongoProducts = ButtonMongoRepository.findAll();
        mongoProducts.forEach(button -> {
            ProductNode productNodeNeo4J = new ProductNode();
            productNodeNeo4J.setName(button.getName());
            productNeo4jRepository.save(productNodeNeo4J);
        });
    }
}
