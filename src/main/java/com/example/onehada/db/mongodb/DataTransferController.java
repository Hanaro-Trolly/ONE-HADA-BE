package com.example.onehada.db.mongodb;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfer")
public class DataTransferController {

    private final DataTransferService dataTransferService;

    public DataTransferController(DataTransferService dataTransferService) {
        this.dataTransferService = dataTransferService;
    }

    @PostMapping
    public String transferData() {
        dataTransferService.transferDataToNeo4j();
        return "Data transfer from MongoDB to Neo4j completed successfully!";
    }
}
