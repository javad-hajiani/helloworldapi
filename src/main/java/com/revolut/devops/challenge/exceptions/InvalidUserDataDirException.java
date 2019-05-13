package com.revolut.devops.challenge.exceptions;

public class InvalidUserDataDirException extends Exception {
    public InvalidUserDataDirException(String error) {
        super(error);
    }
}
