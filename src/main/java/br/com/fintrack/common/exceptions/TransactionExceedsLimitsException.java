package br.com.fintrack.common.exceptions;

public class TransactionExceedsLimitsException extends RuntimeException{

    public TransactionExceedsLimitsException() {
    }

    public TransactionExceedsLimitsException(String message) {
        super(message);
    }
}
