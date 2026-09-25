package com.liora.creditagent.client.dto;

import java.util.UUID;

public record AvaliacaoResponse(
        UUID avaliacaoId,
        String status
) {
}
