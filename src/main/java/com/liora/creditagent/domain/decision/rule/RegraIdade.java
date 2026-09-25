package com.liora.creditagent.domain.decision.rule;

import com.liora.creditagent.client.dto.SolicitacaoResponse;
import com.liora.creditagent.domain.model.ResultadoRegra;
import com.liora.creditagent.domain.model.StatusVerificacao;
import com.liora.creditagent.domain.model.TipoVerificacao;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;

public class RegraIdade implements RegraDecisao<SolicitacaoResponse> {

    private static final int IDADE_MINIMA = 18;

    private final Clock clock;

    public RegraIdade(Clock clock) {
        this.clock = clock;
    }

    @Override
    public ResultadoRegra avaliar(SolicitacaoResponse dados) {

        if (!"PF".equalsIgnoreCase(dados.tipoPessoa())) {
            return new ResultadoRegra(
                    TipoVerificacao.IDADE,
                    StatusVerificacao.APROVADO,
                    "Verificação de idade não aplicável para pessoa jurídica"
            );
        }

        LocalDate dataNascimento = dados.dataNascimento();
        LocalDate hoje = LocalDate.now(clock);

        if (dataNascimento == null || dataNascimento.isAfter(hoje)) {
            return new ResultadoRegra(
                    TipoVerificacao.IDADE,
                    StatusVerificacao.ANALISE_MANUAL,
                    "Data de nascimento ausente ou inválida"
            );
        }

        int idade = Period.between(dataNascimento, hoje).getYears();

        if (idade < IDADE_MINIMA) {
            return new ResultadoRegra(
                    TipoVerificacao.IDADE,
                    StatusVerificacao.REPROVADO,
                    "Solicitante menor de 18 anos"
            );
        }

        return new ResultadoRegra(
                TipoVerificacao.IDADE,
                StatusVerificacao.APROVADO,
                "Solicitante maior de idade"
        );
    }
}