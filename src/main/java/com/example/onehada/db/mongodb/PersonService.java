package com.example.onehada.db.mongodb;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    // 생성자 주입
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    // @Transactional을 통해 트랜잭션 관리
    @Transactional
    public Person createPerson(String name, int age) {
        return personRepository.save(new Person(name, age));
    }

    @Transactional
    public void addFriend(String personName, String friendName) {
        Optional<Person> person = personRepository.findById(personName);
        Optional<Person> friend = personRepository.findById(friendName);

        if (person.isPresent() && friend.isPresent()) {
            person.get().getFriends().add(friend.get());
            personRepository.save(person.get());
        } else {
            throw new RuntimeException("Person or Friend not found!");
        }
    }
    @Transactional(readOnly = true)
    public List<Person> findAllPersons() {
        return personRepository.findAll();
    }
    @Transactional(readOnly = true)
    public Set<Person> findFriends(String personName) {
        return personRepository.findById(personName).map(Person::getFriends).orElse(Set.of());
    }
}
