package com.example.onehada.db.mongodb;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class DataTransferService {

    private final UserRepositori personMongoRepository;
    private final PersonRepository personNeo4jRepository;

    public DataTransferService(UserRepositori personMongoRepository, PersonRepository personNeo4jRepository) {
        this.personMongoRepository = personMongoRepository;
        this.personNeo4jRepository = personNeo4jRepository;
    }

    @Transactional
    public void transferDataToNeo4j() {
        List<User> mongoPersons = personMongoRepository.findAll();
        mongoPersons.forEach(user -> {
            Person personNeo4j = new Person();
            personNeo4j.setName(user.getName());
            personNeo4j.setAge(user.getAge());
            personNeo4jRepository.save(personNeo4j);
        });
    }
}
