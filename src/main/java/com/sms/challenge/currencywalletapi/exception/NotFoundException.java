package com.sms.challenge.currencywalletapi.exception;

/**
 * The type Not found exception.
 */
public class NotFoundException extends RuntimeException {

    /**
     * Instantiates a new Not found exception.
     */
    public NotFoundException() {
    }

    /**
     * Instantiates a new Not found exception.
     *
     * @param var1 the var 1
     */
    public NotFoundException(String var1) {
        super(var1);
    }

    /**
     * Instantiates a new Not found exception.
     *
     * @param var1 the var 1
     * @param var2 the var 2
     */
    public NotFoundException(String var1, Throwable var2) {
        super(var1, var2);
    }

    /**
     * Instantiates a new Not found exception.
     *
     * @param var1 the var 1
     */
    public NotFoundException(Throwable var1) {
        super(var1);
    }
}
