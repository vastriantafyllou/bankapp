package com.vastriantafyllou.bankapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountReadOnlyDTO {
    private Long id;
    private String iban;
    private String accountNumber;
    private BigDecimal balance;
}
