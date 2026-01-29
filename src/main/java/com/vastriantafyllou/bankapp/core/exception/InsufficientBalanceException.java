package com.vastriantafyllou.bankapp.core.exception;

public class InsufficientBalanceException extends Exception {

    public InsufficientBalanceException(String message) {
        super(message);
    }
}
