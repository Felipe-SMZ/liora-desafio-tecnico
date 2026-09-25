package com.liora.creditagent.domain.decision.rule;

import com.liora.creditagent.client.dto.TelefoneValidacaoResponse;
import com.liora.creditagent.domain.model.ResultadoRegra;
import com.liora.creditagent.domain.model.StatusVerificacao;
import com.liora.creditagent.domain.model.TipoVerificacao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RegraTelefone implements RegraDecisao<TelefoneValidacaoResponse> {

    private final int scoreAprovacao;
    private final int scoreReprovacao;

    public RegraTelefone(
            @Value("${liora.decisao.telefone.fraude-score-aprovacao}") int scoreAprovacao,
            @Value("${liora.decisao.telefone.fraude-score-reprovacao}") int scoreReprovacao) {
        this.scoreAprovacao = scoreAprovacao;
        this.scoreReprovacao = scoreReprovacao;
    }

    @Override
    public ResultadoRegra avaliar(TelefoneValidacaoResponse dados) {

        if (dados.fraudeScore() >= scoreReprovacao) {
            return new ResultadoRegra(
                    TipoVerificacao.TELEFONE,
                    StatusVerificacao.REPROVADO,
                    "Telefone com score de fraude alto");

        }

        if (dados.fraudeScore() >= scoreAprovacao || dados.voip()) {
            return new ResultadoRegra(
                    TipoVerificacao.TELEFONE,
                    StatusVerificacao.ANALISE_MANUAL,
                    "Telefone com score de fraude médio e/ou é VOIP");
        }

        return new ResultadoRegra(
                TipoVerificacao.TELEFONE,
                StatusVerificacao.APROVADO,
                "Telefone aprovado");
    }
}
