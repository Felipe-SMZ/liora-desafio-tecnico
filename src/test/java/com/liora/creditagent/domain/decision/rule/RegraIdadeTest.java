package com.liora.creditagent.domain.decision.rule;

import com.liora.creditagent.client.dto.SolicitacaoResponse;
import com.liora.creditagent.domain.model.StatusVerificacao;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class RegraIdadeTest {

    private final Clock clock = Clock.fixed(
            Instant.parse("2026-09-25T12:00:00Z"),
            ZoneOffset.UTC
    );

    private final RegraIdade regra = new RegraIdade(clock);

    @Test
    void deveReprovarQuandoSolicitanteForMenorDeIdade() {

        var solicitacao = criarSolicitacao(
                "PF",
                LocalDate.of(2009, 9, 25)
        );

        var resultado = regra.avaliar(solicitacao);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.REPROVADO);
    }

    @Test
    void deveAprovarQuandoSolicitanteTiverExatamente18Anos() {

        var solicitacao = criarSolicitacao(
                "PF",
                LocalDate.of(2008, 9, 25)
        );

        var resultado = regra.avaliar(solicitacao);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.APROVADO);
    }

    @Test
    void deveAprovarQuandoSolicitanteForMaiorDeIdade() {

        var solicitacao = criarSolicitacao(
                "PF",
                LocalDate.of(1985, 3, 12)
        );

        var resultado = regra.avaliar(solicitacao);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.APROVADO);
    }

    @Test
    void deveAprovarVerificacaoDeIdadeParaPessoaJuridica() {

        var solicitacao = criarSolicitacao(
                "PJ",
                null
        );

        var resultado = regra.avaliar(solicitacao);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.APROVADO);
    }

    @Test
    void deveSolicitarAnaliseManualQuandoDataNascimentoForNula() {

        var solicitacao = criarSolicitacao(
                "PF",
                null
        );

        var resultado = regra.avaliar(solicitacao);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.ANALISE_MANUAL);
    }

    @Test
    void deveSolicitarAnaliseManualQuandoDataNascimentoEstiverNoFuturo() {

        var solicitacao = criarSolicitacao(
                "PF",
                LocalDate.of(2030, 1, 1)
        );

        var resultado = regra.avaliar(solicitacao);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.ANALISE_MANUAL);
    }

    private SolicitacaoResponse criarSolicitacao(
            String tipoPessoa,
            LocalDate dataNascimento
    ) {

        return new SolicitacaoResponse(
                "SOL-2026-001",
                tipoPessoa,
                "432.108.765-09",
                "Carlos Eduardo Mendes",
                dataNascimento,
                "5511987654321",
                "residencial",
                "3001234567",
                "ENEL SP",
                "Rua das Acácias",
                "250",
                "Jardim Paulista",
                "São Paulo",
                "SP",
                "01401-000",
                "Carlos Eduardo Mendes",
                "432.108.765-09",
                LocalDate.of(2026, 4, 15),
                null,
                null,
                null
        );
    }
}