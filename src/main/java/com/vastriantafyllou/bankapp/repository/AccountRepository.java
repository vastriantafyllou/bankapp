package com.vastriantafyllou.bankapp.repository;

import com.vastriantafyllou.bankapp.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByIban(String iban);
    boolean existsByIban(String iban);
}
