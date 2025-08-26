package com.food.ordering.system.payment.service.application.exception;

public class PaymentApplicationServiceException extends RuntimeException {

    public PaymentApplicationServiceException(String message) {
        super(message);
    }

    public PaymentApplicationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
