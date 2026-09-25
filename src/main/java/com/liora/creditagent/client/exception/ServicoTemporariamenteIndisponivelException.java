package com.liora.creditagent.client.exception;

public class ServicoTemporariamenteIndisponivelException extends RuntimeException {

    private final Integer retryAfter;

    public ServicoTemporariamenteIndisponivelException(
            String message,
            Integer retryAfter
    ) {
        super(message);
        this.retryAfter = retryAfter;
    }

    public Integer getRetryAfter() {
        return retryAfter;
    }
}
