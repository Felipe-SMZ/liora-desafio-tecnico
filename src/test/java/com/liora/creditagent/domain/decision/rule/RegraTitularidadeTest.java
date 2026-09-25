package com.liora.creditagent.domain.decision.rule;

import com.liora.creditagent.client.dto.SolicitacaoResponse;
import com.liora.creditagent.domain.model.StatusVerificacao;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class RegraTitularidadeTest {

    private final RegraTitularidade regra = new RegraTitularidade();

    @Test
    void deveAprovarQuandoSolicitanteForTitularDaConta() {

        var solicitacao = criarSolicitacao(
                "PF",
                "432.108.765-09",
                "432.108.765-09",
                null,
                null
        );

        var resultado = regra.avaliar(solicitacao);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.APROVADO);
    }

    @Test
    void deveSolicitarAnaliseManualQuandoPfTiverTitularidadeDivergenteComContratoVigente() {

        var solicitacao = criarSolicitacao(
                "PF",
                "432.108.765-09",
                "111.222.333-44",
                true,
                null
        );

        var resultado = regra.avaliar(solicitacao);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.ANALISE_MANUAL);
    }

    @Test
    void deveSolicitarAnaliseManualQuandoPjTiverTitularidadeDivergenteComVinculoSocietario() {

        var solicitacao = criarSolicitacao(
                "PJ",
                "12.345.678/0001-90",
                "98.765.432/0001-10",
                null,
                "socio"
        );

        var resultado = regra.avaliar(solicitacao);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.ANALISE_MANUAL);
    }

    @Test
    void deveReprovarQuandoTitularidadeForDivergenteSemJustificativa() {

        var solicitacao = criarSolicitacao(
                "PF",
                "432.108.765-09",
                "111.222.333-44",
                false,
                null
        );

        var resultado = regra.avaliar(solicitacao);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.REPROVADO);
    }

    private SolicitacaoResponse criarSolicitacao(
            String tipoPessoa,
            String cpfCnpj,
            String titularCpfCnpj,
            Boolean contratoVigente,
            String vinculoEmpresa
    ) {

        return new SolicitacaoResponse(
                "SOL-2026-001",
                tipoPessoa,
                cpfCnpj,
                "Carlos Eduardo Mendes",
                LocalDate.of(1985, 3, 12),
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
                titularCpfCnpj,
                LocalDate.of(2026, 4, 15),
                contratoVigente,
                null,
                vinculoEmpresa
        );
    }
}