package com.vastriantafyllou.bankapp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountInsertDTO {

    @NotBlank(message = "Το IBAN είναι υποχρεωτικό")
    @Size(min = 5, message = "Το IBAN πρέπει να έχει τουλάχιστον 5 χαρακτήρες")
    private String iban;

    @NotBlank(message = "Το Account Number είναι υποχρεωτικό")
    @Size(min = 5, message = "Το Account Number πρέπει να έχει τουλάχιστον 5 χαρακτήρες")
    private String accountNumber;

    @NotNull(message = "Το αρχικό υπόλοιπο είναι υποχρεωτικό")
    @DecimalMin(value = "0.0", message = "Το αρχικό υπόλοιπο δεν μπορεί να είναι αρνητικό")
    private BigDecimal balance;
}
