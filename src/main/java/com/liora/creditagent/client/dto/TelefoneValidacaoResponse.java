package com.liora.creditagent.client.dto;

public record TelefoneValidacaoResponse(
        String telefone,
        boolean voip,
        int fraudeScore
) {
}
