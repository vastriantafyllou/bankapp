package com.vastriantafyllou.bankapp.service;

import com.vastriantafyllou.bankapp.core.exception.AccountAlreadyExistsException;
import com.vastriantafyllou.bankapp.core.exception.AccountNumberAlreadyExistsException;
import com.vastriantafyllou.bankapp.core.exception.AccountNotFoundException;
import com.vastriantafyllou.bankapp.core.exception.InvalidTransferException;
import com.vastriantafyllou.bankapp.core.exception.InsufficientBalanceException;
import com.vastriantafyllou.bankapp.core.exception.NegativeAmountException;
import com.vastriantafyllou.bankapp.dto.AccountInsertDTO;
import com.vastriantafyllou.bankapp.dto.AccountReadOnlyDTO;
import com.vastriantafyllou.bankapp.mapper.Mapper;
import com.vastriantafyllou.bankapp.model.Account;
import com.vastriantafyllou.bankapp.model.AccountTransaction;
import com.vastriantafyllou.bankapp.model.TransactionType;
import com.vastriantafyllou.bankapp.repository.AccountRepository;
import com.vastriantafyllou.bankapp.repository.AccountTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements IAccountService {

    private final AccountRepository accountRepository;
    private final AccountTransactionRepository accountTransactionRepository;

    @Override
    @Transactional
    public AccountReadOnlyDTO createAccount(AccountInsertDTO dto) throws AccountAlreadyExistsException, AccountNumberAlreadyExistsException {
        if (accountRepository.existsByIban(dto.getIban())) {
            throw new AccountAlreadyExistsException("Ο λογαριασμός με IBAN " + dto.getIban() + " υπάρχει ήδη");
        }
        if (accountRepository.existsByAccountNumber(dto.getAccountNumber())) {
            throw new AccountNumberAlreadyExistsException("Ο λογαριασμός με Account Number " + dto.getAccountNumber() + " υπάρχει ήδη");
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

        Account account = accountRepository.findByIbanForUpdate(iban)
                .orElseThrow(() -> new AccountNotFoundException("Ο λογαριασμός με IBAN " + iban + " δεν βρέθηκε"));

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        accountTransactionRepository.save(AccountTransaction.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(amount)
                .createdAt(LocalDateTime.now())
                .balanceAfter(account.getBalance())
                .build());
    }

    @Override
    @Transactional
    public void withdraw(String iban, BigDecimal amount) throws NegativeAmountException, AccountNotFoundException, InsufficientBalanceException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeAmountException("Το ποσό ανάληψης πρέπει να είναι θετικό");
        }

        Account account = accountRepository.findByIbanForUpdate(iban)
                .orElseThrow(() -> new AccountNotFoundException("Ο λογαριασμός με IBAN " + iban + " δεν βρέθηκε"));

        if (amount.compareTo(account.getBalance()) > 0) {
            throw new InsufficientBalanceException("Ανεπαρκές υπόλοιπο. Διαθέσιμο: " + account.getBalance() + " €");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        accountTransactionRepository.save(AccountTransaction.builder()
                .account(account)
                .type(TransactionType.WITHDRAW)
                .amount(amount)
                .createdAt(LocalDateTime.now())
                .balanceAfter(account.getBalance())
                .build());
    }

    @Override
    @Transactional
    public void transfer(String fromIban, String toIban, BigDecimal amount) throws NegativeAmountException, AccountNotFoundException, InsufficientBalanceException, InvalidTransferException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeAmountException("Το ποσό μεταφοράς πρέπει να είναι θετικό");
        }
        if (fromIban.equals(toIban)) {
            throw new InvalidTransferException("Δεν επιτρέπεται μεταφορά στον ίδιο λογαριασμό");
        }

        String firstIban = fromIban.compareTo(toIban) < 0 ? fromIban : toIban;
        String secondIban = fromIban.compareTo(toIban) < 0 ? toIban : fromIban;

        Account first = accountRepository.findByIbanForUpdate(firstIban)
                .orElseThrow(() -> new AccountNotFoundException("Ο λογαριασμός με IBAN " + firstIban + " δεν βρέθηκε"));
        Account second = accountRepository.findByIbanForUpdate(secondIban)
                .orElseThrow(() -> new AccountNotFoundException("Ο λογαριασμός με IBAN " + secondIban + " δεν βρέθηκε"));

        Account fromAccount = fromIban.equals(firstIban) ? first : second;
        Account toAccount = toIban.equals(firstIban) ? first : second;

        if (amount.compareTo(fromAccount.getBalance()) > 0) {
            throw new InsufficientBalanceException("Ανεπαρκές υπόλοιπο. Διαθέσιμο: " + fromAccount.getBalance() + " €");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        LocalDateTime now = LocalDateTime.now();
        accountTransactionRepository.save(AccountTransaction.builder()
                .account(fromAccount)
                .type(TransactionType.TRANSFER_OUT)
                .amount(amount)
                .createdAt(now)
                .counterpartyIban(toIban)
                .balanceAfter(fromAccount.getBalance())
                .build());

        accountTransactionRepository.save(AccountTransaction.builder()
                .account(toAccount)
                .type(TransactionType.TRANSFER_IN)
                .amount(amount)
                .createdAt(now)
                .counterpartyIban(fromIban)
                .balanceAfter(toAccount.getBalance())
                .build());
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
    @Transactional(readOnly = true)
    public List<AccountTransaction> getTransactionHistory(String iban) throws AccountNotFoundException {
        if (!accountRepository.existsByIban(iban)) {
            throw new AccountNotFoundException("Ο λογαριασμός με IBAN " + iban + " δεν βρέθηκε");
        }
        return accountTransactionRepository.findByAccount_IbanOrderByCreatedAtDesc(iban);
    }

    @Override
    @Transactional
    public void deleteAccount(String iban) throws AccountNotFoundException {
        Account account = accountRepository.findByIban(iban)
                .orElseThrow(() -> new AccountNotFoundException("Ο λογαριασμός με IBAN " + iban + " δεν βρέθηκε"));
        accountTransactionRepository.deleteByAccount_Iban(iban);
        accountRepository.delete(account);
    }
}
