package com.paulovargas.erpapi.controllers;

import com.paulovargas.erpapi.entities.Person;
import com.paulovargas.erpapi.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/peoples")
public class PersonController {

    @Autowired
    private PersonRepository repository;

    @GetMapping
    public List<Person> findAll() {
        return repository.findAll();
    }

    @GetMapping(value ="/{id}")
    public Person findById(@PathVariable Integer id){
        Person result = repository.findById(id).get();
        return result;
    }

    @PostMapping
    public Person insert(@RequestBody Person person) {
        return repository.save(person);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Person> update(@PathVariable Integer id, @RequestBody Person updatedPerson) {
        return repository.findById(id)
                .map(person -> {
                    person.setName(updatedPerson.getName());
                    person.setCpf(updatedPerson.getCpf());
                    person.setAddress(updatedPerson.getAddress());
                    Person saved = repository.save(person);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build(); // 204
        } else {
            return ResponseEntity.notFound().build(); // 404
        }
    }
}
