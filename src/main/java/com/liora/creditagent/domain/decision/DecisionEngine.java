package com.liora.creditagent.domain.decision;

import com.liora.creditagent.domain.model.Decisao;
import com.liora.creditagent.domain.model.ResultadoDecisao;
import com.liora.creditagent.domain.model.ResultadoRegra;
import com.liora.creditagent.domain.model.StatusVerificacao;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DecisionEngine {

    public ResultadoDecisao decidir(List<ResultadoRegra> verificacoes) {

        if (verificacoes == null || verificacoes.isEmpty()) {
            throw new IllegalArgumentException(
                    "A lista de verificações não pode ser nula ou vazia"
            );
        }

        boolean existeReprovacao = verificacoes.stream()
                .anyMatch(verificacao -> verificacao.status() == StatusVerificacao.REPROVADO);

        if (existeReprovacao) {
            return new ResultadoDecisao(Decisao.REPROVADO, verificacoes);
        }

        boolean existeAnaliseManual = verificacoes.stream()
                .anyMatch(verificacao -> verificacao.status() == StatusVerificacao.ANALISE_MANUAL);

        if (existeAnaliseManual) {
            return new ResultadoDecisao(Decisao.ANALISE_MANUAL, verificacoes);
        }

        return new ResultadoDecisao(Decisao.APROVADO, verificacoes);

    }
}
