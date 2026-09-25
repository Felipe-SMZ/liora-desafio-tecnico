package com.liora.creditagent.domain.decision.rule;

import com.liora.creditagent.client.dto.DebitosResponse;
import com.liora.creditagent.domain.model.ResultadoRegra;
import com.liora.creditagent.domain.model.StatusVerificacao;
import com.liora.creditagent.domain.model.TipoVerificacao;

import java.math.BigDecimal;

public class RegraDebitos implements RegraDecisao<DebitosResponse> {

    @Override
    public ResultadoRegra avaliar(DebitosResponse dados) {

        if (dados.corteProgramado()) {
            return new ResultadoRegra(
                    TipoVerificacao.DEBITOS_INSTALACAO,
                    StatusVerificacao.REPROVADO,
                    "Instalação possui corte programado"
            );
        }

        boolean possuiDebitos =
                "inadimplente".equalsIgnoreCase(dados.status())
                        || dados.faturasEmAtraso() > 0
                        || dados.debitosTotal().compareTo(BigDecimal.ZERO) > 0;

        if (possuiDebitos) {
            return new ResultadoRegra(
                    TipoVerificacao.DEBITOS_INSTALACAO,
                    StatusVerificacao.ANALISE_MANUAL,
                    "Instalação possui débitos pendentes"
            );
        }

        return new ResultadoRegra(
                TipoVerificacao.DEBITOS_INSTALACAO,
                StatusVerificacao.APROVADO,
                "Instalação sem débitos pendentes"
        );
    }
}