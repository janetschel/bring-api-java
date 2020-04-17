package com.janetschel.bring.exception;

public class AlreadyLoggedInException extends Exception {
    public AlreadyLoggedInException(String message) {
        super(message);
    }
}
