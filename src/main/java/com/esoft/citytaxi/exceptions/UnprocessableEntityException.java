package com.esoft.citytaxi.exceptions;

/**
 * The type Unprocessable entity exception.
 *
 */
public class UnprocessableEntityException extends RuntimeException {
    /**
     * Instantiates a new Unprocessable entity exception.
     */
    public UnprocessableEntityException() {
        super();
    }

    /**
     * Instantiates a new Unprocessable entity exception.
     *
     * @param message the message
     */
    public UnprocessableEntityException(String message) {
        super(message);
    }
}
