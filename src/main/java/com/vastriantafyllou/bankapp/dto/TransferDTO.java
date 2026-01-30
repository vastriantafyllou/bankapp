package com.vastriantafyllou.bankapp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferDTO {

    @NotBlank(message = "Το IBAN παραλήπτη είναι υποχρεωτικό")
    private String toIban;

    @NotNull(message = "Το ποσό είναι υποχρεωτικό")
    @DecimalMin(value = "0.01", message = "Το ποσό πρέπει να είναι μεγαλύτερο από 0")
    private BigDecimal amount;
}
