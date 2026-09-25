package com.liora.creditagent.domain.decision.rule;

import com.liora.creditagent.client.dto.BlacklistResponse;
import com.liora.creditagent.domain.model.ResultadoRegra;
import com.liora.creditagent.domain.model.StatusVerificacao;
import com.liora.creditagent.domain.model.TipoVerificacao;

public class RegraBlacklist implements RegraDecisao<BlacklistResponse> {

    @Override
    public ResultadoRegra avaliar(BlacklistResponse dados) {

        if (dados.blacklist()) {
            return new ResultadoRegra(
                    TipoVerificacao.BLACKLIST,
                    StatusVerificacao.REPROVADO,
                    "CPF consta na blacklist"
            );
        }

        return new ResultadoRegra(
                TipoVerificacao.BLACKLIST,
                StatusVerificacao.APROVADO,
                "CPF não consta na blacklist"
        );
    }
}