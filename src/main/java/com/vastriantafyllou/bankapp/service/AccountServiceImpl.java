package com.vastriantafyllou.bankapp.service;

import com.vastriantafyllou.bankapp.core.exception.AccountAlreadyExistsException;
import com.vastriantafyllou.bankapp.core.exception.AccountNotFoundException;
import com.vastriantafyllou.bankapp.core.exception.InsufficientBalanceException;
import com.vastriantafyllou.bankapp.core.exception.NegativeAmountException;
import com.vastriantafyllou.bankapp.dto.AccountInsertDTO;
import com.vastriantafyllou.bankapp.dto.AccountReadOnlyDTO;
import com.vastriantafyllou.bankapp.mapper.Mapper;
import com.vastriantafyllou.bankapp.model.Account;
import com.vastriantafyllou.bankapp.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements IAccountService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public AccountReadOnlyDTO createAccount(AccountInsertDTO dto) throws AccountAlreadyExistsException {
        if (accountRepository.existsByIban(dto.getIban())) {
            throw new AccountAlreadyExistsException("Ο λογαριασμός με IBAN " + dto.getIban() + " υπάρχει ήδη");
        }
        Account account = Mapper.mapToEntity(dto);
        Account savedAccount = accountRepository.save(account);
        return Mapper.mapToReadOnlyDTO(savedAccount);
    }

    @Override
    @Transactional
    public void deposit(String iban, BigDecimal amount) throws NegativeAmountException, AccountNotFoundException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeAmountException("Το ποσό κατάθεσης πρέπει να είναι θετικό");
        }

        Account account = accountRepository.findByIban(iban)
                .orElseThrow(() -> new AccountNotFoundException("Ο λογαριασμός με IBAN " + iban + " δεν βρέθηκε"));

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void withdraw(String iban, BigDecimal amount) throws NegativeAmountException, AccountNotFoundException, InsufficientBalanceException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeAmountException("Το ποσό ανάληψης πρέπει να είναι θετικό");
        }

        Account account = accountRepository.findByIban(iban)
                .orElseThrow(() -> new AccountNotFoundException("Ο λογαριασμός με IBAN " + iban + " δεν βρέθηκε"));

        if (amount.compareTo(account.getBalance()) > 0) {
            throw new InsufficientBalanceException("Ανεπαρκές υπόλοιπο. Διαθέσιμο: " + account.getBalance() + " €");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getBalance(String iban) throws AccountNotFoundException {
        Account account = accountRepository.findByIban(iban)
                .orElseThrow(() -> new AccountNotFoundException("Ο λογαριασμός με IBAN " + iban + " δεν βρέθηκε"));
        return account.getBalance();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountReadOnlyDTO> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(Mapper::mapToReadOnlyDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AccountReadOnlyDTO getAccountByIban(String iban) throws AccountNotFoundException {
        Account account = accountRepository.findByIban(iban)
                .orElseThrow(() -> new AccountNotFoundException("Ο λογαριασμός με IBAN " + iban + " δεν βρέθηκε"));
        return Mapper.mapToReadOnlyDTO(account);
    }

    @Override
    @Transactional
    public void deleteAccount(String iban) throws AccountNotFoundException {
        Account account = accountRepository.findByIban(iban)
                .orElseThrow(() -> new AccountNotFoundException("Ο λογαριασμός με IBAN " + iban + " δεν βρέθηκε"));
        accountRepository.delete(account);
    }
}
