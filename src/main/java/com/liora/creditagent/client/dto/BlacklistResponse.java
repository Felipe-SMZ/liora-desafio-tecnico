package com.liora.creditagent.client.dto;

public record BlacklistResponse(
        String cpf,
        boolean blacklist,
        String motivo
) {
}
