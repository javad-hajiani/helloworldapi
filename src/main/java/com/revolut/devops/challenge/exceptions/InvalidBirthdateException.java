package com.revolut.devops.challenge.exceptions;

public class InvalidBirthdateException extends Exception {
    public InvalidBirthdateException(String error) {
        super(error);
    }
}
