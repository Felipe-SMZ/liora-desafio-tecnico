package com.liora.creditagent.client.dto;

import java.util.Map;

public record AvaliacaoRequest(
        String solicitacaoId,
        String cpfCnpj,
        String decisao,
        Integer scoreRisco,
        Map<String, String> verificacoes,
        String justificativa,
        String agenteVersao
) {
}
