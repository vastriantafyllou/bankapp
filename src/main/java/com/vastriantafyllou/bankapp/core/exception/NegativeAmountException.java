package com.vastriantafyllou.bankapp.core.exception;

public class NegativeAmountException extends Exception {

    public NegativeAmountException(String message) {
        super(message);
    }
}
