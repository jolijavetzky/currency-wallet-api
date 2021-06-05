package com.sms.challenge.currencywalletapi.exception;

/**
 * The type Validation exception.
 */
public class ValidationException extends RuntimeException {

    /**
     * Instantiates a new Validation exception.
     */
    public ValidationException() {
    }

    /**
     * Instantiates a new Validation exception.
     *
     * @param var1 the var 1
     */
    public ValidationException(String var1) {
        super(var1);
    }

    /**
     * Instantiates a new Validation exception.
     *
     * @param var1 the var 1
     * @param var2 the var 2
     */
    public ValidationException(String var1, Throwable var2) {
        super(var1, var2);
    }

    /**
     * Instantiates a new Validation exception.
     *
     * @param var1 the var 1
     */
    public ValidationException(Throwable var1) {
        super(var1);
    }
}
