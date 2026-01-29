package com.vastriantafyllou.bankapp.core.exception;

public class AccountAlreadyExistsException extends Exception {

    public AccountAlreadyExistsException(String message) {
        super(message);
    }
}
