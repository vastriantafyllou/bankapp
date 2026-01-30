package com.vastriantafyllou.bankapp.service;

import com.vastriantafyllou.bankapp.core.exception.AccountAlreadyExistsException;
import com.vastriantafyllou.bankapp.core.exception.AccountNumberAlreadyExistsException;
import com.vastriantafyllou.bankapp.core.exception.AccountNotFoundException;
import com.vastriantafyllou.bankapp.core.exception.InvalidTransferException;
import com.vastriantafyllou.bankapp.core.exception.InsufficientBalanceException;
import com.vastriantafyllou.bankapp.core.exception.NegativeAmountException;
import com.vastriantafyllou.bankapp.dto.AccountInsertDTO;
import com.vastriantafyllou.bankapp.dto.AccountReadOnlyDTO;
import com.vastriantafyllou.bankapp.model.AccountTransaction;

import java.math.BigDecimal;
import java.util.List;

public interface IAccountService {
    AccountReadOnlyDTO createAccount(AccountInsertDTO dto) throws AccountAlreadyExistsException, AccountNumberAlreadyExistsException;
    void deposit(String iban, BigDecimal amount) throws NegativeAmountException, AccountNotFoundException;
    void withdraw(String iban, BigDecimal amount) throws NegativeAmountException, AccountNotFoundException, InsufficientBalanceException;
    void transfer(String fromIban, String toIban, BigDecimal amount) throws NegativeAmountException, AccountNotFoundException, InsufficientBalanceException, InvalidTransferException;
    BigDecimal getBalance(String iban) throws AccountNotFoundException;
    List<AccountReadOnlyDTO> getAllAccounts();
    AccountReadOnlyDTO getAccountByIban(String iban) throws AccountNotFoundException;
    List<AccountTransaction> getTransactionHistory(String iban) throws AccountNotFoundException;
    void deleteAccount(String iban) throws AccountNotFoundException;
}
