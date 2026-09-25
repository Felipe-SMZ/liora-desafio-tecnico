package com.liora.creditagent.client.dto;

public record PaginationResponse(
        int total,
        int limit,
        int offset
) {
}
