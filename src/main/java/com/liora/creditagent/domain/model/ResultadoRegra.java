package com.liora.creditagent.domain.model;

public record ResultadoRegra(
        TipoVerificacao regra,
        StatusVerificacao status,
        String motivo
) {
}
