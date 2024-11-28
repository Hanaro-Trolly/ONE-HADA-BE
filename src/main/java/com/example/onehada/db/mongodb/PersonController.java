package com.example.onehada.db.mongodb;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public List<Person> getAllPersons() {
        return personService.findAllPersons();
    }

    @PostMapping
    public Person createPerson(@RequestBody Person person) {
        return personService.createPerson(person.getName(), person.getAge());
    }
    @PostMapping("/addFriend")
    public String addFriend(@RequestParam String personName, @RequestParam String friendName) {
        personService.addFriend(personName, friendName);
        return personName + " is now friends with " + friendName;
    }

    @GetMapping("/{name}/friends")
    public Set<Person> getFriends(@PathVariable String name) {
        return personService.findFriends(name);
    }
}
