package com.liora.creditagent.client.dto;

public record ApiErrorResponse(
        String error,
        String message,
        Integer retryAfter
) {
}
