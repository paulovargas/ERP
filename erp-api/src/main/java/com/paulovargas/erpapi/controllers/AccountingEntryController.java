package com.paulovargas.erpapi.controllers;

import com.paulovargas.erpapi.entities.*;
import com.paulovargas.erpapi.repositories.AccountRepository;
import com.paulovargas.erpapi.repositories.AccountingEntryRepository;
import com.paulovargas.erpapi.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/accountingEntries")
public class AccountingEntryController {

    @Autowired
    private AccountingEntryRepository accountingEntryRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping
    public List<AccountingEntry> findAll() {
        return accountingEntryRepository.findAll();
    }

    @GetMapping(value ="/{id}")
    public AccountingEntry findById(@PathVariable Integer id){
        AccountingEntry result = accountingEntryRepository.findById(id).get();
        return result;
    }

    @GetMapping("/balance/{personId}")
    public ResponseEntity<List<AccountBalanceDTO>> getBalanceByPersonId(@PathVariable Integer personId) {
        List<Object[]> results = accountingEntryRepository.findAccountBalanceByPersonId(personId);

        List<AccountBalanceDTO> dtos = results.stream()
                .map(obj -> new AccountBalanceDTO(
                        (Integer) obj[0],           // id da conta
                        obj[1].toString(),          // nÃºmero da conta (String ou Integer convertido)
                        (Double) obj[2]             // saldo
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/bankStatement/{accountId}")
    public ResponseEntity<List<BankStatement>> getSimpleEntries(@PathVariable Integer accountId) {
        List<BankStatement> entries = accountingEntryRepository.findAmountsAndTimestampsByAccountId(accountId);

        if (entries.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(entries);
    }

    @PostMapping
    public ResponseEntity<AccountingEntry> insert(@RequestBody AccountingEntryDTO dto) {
        Optional<Person> optionalPerson = personRepository.findById(dto.getPersonId());
        Optional<Account> optionalAccount = accountRepository.findById(dto.getAccountId());

        if (!optionalPerson.isPresent() || !optionalAccount.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        AccountingEntry entry = new AccountingEntry();
        entry.setAmount(dto.getAmount());
        entry.setOperation(dto.getOperation());
        entry.setAccount(optionalAccount.get());
        entry.setPerson(optionalPerson.get());

        AccountingEntry saved = accountingEntryRepository.save(entry);

        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountingEntry> update(@PathVariable Integer id, @RequestBody AccountingEntry updatedAccountingEntry) {
        return accountingEntryRepository.findById(id)
                .map(AccountingEntry -> {
                    AccountingEntry.setPerson (updatedAccountingEntry.getPerson());
                    AccountingEntry.setAccount(updatedAccountingEntry.getAccount());
                    AccountingEntry.setAmount(updatedAccountingEntry.getAmount());
                    AccountingEntry.setOperation(updatedAccountingEntry.getOperation());
                    AccountingEntry saved = accountingEntryRepository.save(AccountingEntry);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (accountingEntryRepository.existsById(id)) {
            accountingEntryRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // 204
        } else {
            return ResponseEntity.notFound().build(); // 404
        }
    }
}
