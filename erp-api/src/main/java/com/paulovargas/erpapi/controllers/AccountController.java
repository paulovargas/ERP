package com.paulovargas.erpapi.controllers;

import com.paulovargas.erpapi.entities.Account;
import com.paulovargas.erpapi.entities.AccountDTO;
import com.paulovargas.erpapi.entities.Person;
import com.paulovargas.erpapi.repositories.AccountRepository;
import com.paulovargas.erpapi.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/accounts")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PersonRepository personRepository;

    @GetMapping
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @GetMapping(value ="/{id}")
    public Account findById(@PathVariable Integer id){
        Account result = accountRepository.findById(id).get();
        return result;
    }

    @GetMapping("/verify/{numberAccount}")
    public ResponseEntity<Account> findByNumberAccount(@PathVariable String numberAccount) {
        Optional<Account> result = accountRepository.findByNumberAccount(numberAccount);

        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return result.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Account> insert(@RequestBody AccountDTO dto) {
        Optional<Person> optionalPerson = personRepository.findById(dto.getPersonId());

        if (!optionalPerson.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Account account = new Account();
        account.setNumberAccount(dto.getNumberAccount());
        account.setPerson(optionalPerson.get());

        Account saved = accountRepository.save(account);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody AccountDTO dto) {
        return accountRepository.findById(id)
                .map(account -> {
                    Optional<Person> person = personRepository.findById(dto.getPersonId());
                    if (!person.isPresent()) {
                        return ResponseEntity.badRequest().body("Pessoa nÃ£o encontrada com ID: " + dto.getPersonId());
                    }

                    Optional<Account> duplicate = accountRepository.findByNumberAccount(dto.getNumberAccount());
                    if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                        return ResponseEntity.badRequest().body("JÃ¡ existe outra conta com esse nÃºmero.");
                    }

                    account.setNumberAccount(dto.getNumberAccount());
                    account.setPerson(person.get());
                    Account saved = accountRepository.save(account);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (accountRepository.existsById(id)) {
            accountRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // 204
        } else {
            return ResponseEntity.notFound().build(); // 404
        }
    }
}
