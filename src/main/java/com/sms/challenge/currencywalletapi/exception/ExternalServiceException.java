package com.sms.challenge.currencywalletapi.exception;

/**
 * The type External service exception.
 */
public class ExternalServiceException extends RuntimeException {

    /**
     * Instantiates a new External service exception.
     */
    public ExternalServiceException() {
    }

    /**
     * Instantiates a new External service exception.
     *
     * @param var1 the var 1
     */
    public ExternalServiceException(String var1) {
        super(var1);
    }

    /**
     * Instantiates a new External service exception.
     *
     * @param var1 the var 1
     * @param var2 the var 2
     */
    public ExternalServiceException(String var1, Throwable var2) {
        super(var1, var2);
    }

    /**
     * Instantiates a new External service exception.
     *
     * @param var1 the var 1
     */
    public ExternalServiceException(Throwable var1) {
        super(var1);
    }
}
