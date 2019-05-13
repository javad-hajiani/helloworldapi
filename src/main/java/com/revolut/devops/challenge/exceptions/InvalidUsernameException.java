package com.revolut.devops.challenge.exceptions;

public class InvalidUsernameException extends Exception {
    public InvalidUsernameException(String error) {
        super(error);
    }
}
