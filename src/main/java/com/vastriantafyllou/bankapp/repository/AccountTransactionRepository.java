package com.vastriantafyllou.bankapp.repository;

import com.vastriantafyllou.bankapp.model.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {
    List<AccountTransaction> findByAccount_IbanOrderByCreatedAtDesc(String iban);
    void deleteByAccount_Iban(String iban);
}
