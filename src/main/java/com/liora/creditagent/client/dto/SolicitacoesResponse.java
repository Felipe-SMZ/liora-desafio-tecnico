package com.liora.creditagent.client.dto;

import java.util.List;

public record SolicitacoesResponse(
        List<SolicitacaoResponse> solicitacoes,
        PaginationResponse pagination
) {
}