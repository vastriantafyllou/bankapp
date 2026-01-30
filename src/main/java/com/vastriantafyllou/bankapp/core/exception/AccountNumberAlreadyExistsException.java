package com.vastriantafyllou.bankapp.core.exception;

public class AccountNumberAlreadyExistsException extends Exception {

    public AccountNumberAlreadyExistsException(String message) {
        super(message);
    }
}
