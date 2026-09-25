package com.liora.creditagent.domain.model;

import java.util.List;

public record ResultadoDecisao(
        Decisao decisao,
        List<ResultadoRegra> verificacoes
) {
}
