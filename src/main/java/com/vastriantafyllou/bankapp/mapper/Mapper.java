package com.vastriantafyllou.bankapp.mapper;

import com.vastriantafyllou.bankapp.dto.AccountInsertDTO;
import com.vastriantafyllou.bankapp.dto.AccountReadOnlyDTO;
import com.vastriantafyllou.bankapp.model.Account;

public class Mapper {

    private Mapper() {
    }

    public static Account mapToEntity(AccountInsertDTO dto) {
        return Account.builder()
                .iban(dto.getIban())
                .balance(dto.getBalance())
                .build();
    }

    public static AccountReadOnlyDTO mapToReadOnlyDTO(Account account) {
        return new AccountReadOnlyDTO(account.getId(), account.getIban(), account.getBalance());
    }
}
