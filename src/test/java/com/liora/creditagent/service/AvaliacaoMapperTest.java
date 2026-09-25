package com.liora.creditagent.service;

import com.liora.creditagent.client.dto.SolicitacaoResponse;
import com.liora.creditagent.domain.model.Decisao;
import com.liora.creditagent.domain.model.ResultadoDecisao;
import com.liora.creditagent.domain.model.ResultadoRegra;
import com.liora.creditagent.domain.model.StatusVerificacao;
import com.liora.creditagent.domain.model.TipoVerificacao;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AvaliacaoMapperTest {

    private final AvaliacaoMapper mapper =
            new AvaliacaoMapper("v1.0.0");

    @Test
    void deveMapearAvaliacaoAprovada() {

        var solicitacao = criarSolicitacao();

        var verificacoes = List.of(
                resultado(
                        TipoVerificacao.IDADE,
                        StatusVerificacao.APROVADO,
                        "Solicitante maior de idade"
                ),
                resultado(
                        TipoVerificacao.BLACKLIST,
                        StatusVerificacao.APROVADO,
                        "CPF não consta na blacklist"
                ),
                resultado(
                        TipoVerificacao.ENDERECO,
                        StatusVerificacao.APROVADO,
                        "Endereço validado com sucesso"
                )
        );

        var resultadoDecisao = new ResultadoDecisao(
                Decisao.APROVADO,
                verificacoes
        );

        var request = mapper.mapear(
                solicitacao,
                resultadoDecisao
        );

        assertThat(request.solicitacaoId())
                .isEqualTo("SOL-2026-001");

        assertThat(request.cpfCnpj())
                .isEqualTo("432.108.765-09");

        assertThat(request.decisao())
                .isEqualTo("aprovado");

        assertThat(request.scoreRisco())
                .isNull();

        assertThat(request.verificacoes())
                .containsEntry("idade", "aprovado")
                .containsEntry("blacklist", "aprovado")
                .containsEntry("endereco", "aprovado");

        assertThat(request.justificativa())
                .isEqualTo("Todas as verificações foram aprovadas.");

        assertThat(request.agenteVersao())
                .isEqualTo("v1.0.0");
    }

    @Test
    void deveMapearAvaliacaoParaAnaliseManual() {

        var solicitacao = criarSolicitacao();

        var verificacoes = List.of(
                resultado(
                        TipoVerificacao.IDADE,
                        StatusVerificacao.APROVADO,
                        "Solicitante maior de idade"
                ),
                resultado(
                        TipoVerificacao.ENDERECO,
                        StatusVerificacao.ANALISE_MANUAL,
                        "Endereço inválido. CEP sugerido: 01310-100"
                ),
                resultado(
                        TipoVerificacao.TELEFONE,
                        StatusVerificacao.ANALISE_MANUAL,
                        "Telefone VoIP requer análise"
                ),
                resultado(
                        TipoVerificacao.BLACKLIST,
                        StatusVerificacao.APROVADO,
                        "CPF não consta na blacklist"
                )
        );

        var resultadoDecisao = new ResultadoDecisao(
                Decisao.ANALISE_MANUAL,
                verificacoes
        );

        var request = mapper.mapear(
                solicitacao,
                resultadoDecisao
        );

        assertThat(request.decisao())
                .isEqualTo("analise_manual");

        assertThat(request.verificacoes())
                .containsEntry("idade", "aprovado")
                .containsEntry("endereco", "analise_manual")
                .containsEntry("telefone", "analise_manual")
                .containsEntry("blacklist", "aprovado");

        assertThat(request.justificativa())
                .contains("Endereço inválido. CEP sugerido: 01310-100")
                .contains("Telefone VoIP requer análise")
                .doesNotContain("Solicitante maior de idade")
                .doesNotContain("CPF não consta na blacklist");

        assertThat(request.agenteVersao())
                .isEqualTo("v1.0.0");
    }

    @Test
    void deveMapearAvaliacaoReprovadaPriorizandoMotivosDeReprovacao() {

        var solicitacao = criarSolicitacao();

        var verificacoes = List.of(
                resultado(
                        TipoVerificacao.IDADE,
                        StatusVerificacao.REPROVADO,
                        "Solicitante menor de 18 anos"
                ),
                resultado(
                        TipoVerificacao.TITULARIDADE,
                        StatusVerificacao.ANALISE_MANUAL,
                        "Titularidade divergente com contrato de locação vigente"
                ),
                resultado(
                        TipoVerificacao.BLACKLIST,
                        StatusVerificacao.APROVADO,
                        "CPF não consta na blacklist"
                )
        );

        var resultadoDecisao = new ResultadoDecisao(
                Decisao.REPROVADO,
                verificacoes
        );

        var request = mapper.mapear(
                solicitacao,
                resultadoDecisao
        );

        assertThat(request.decisao())
                .isEqualTo("reprovado");

        assertThat(request.verificacoes())
                .containsEntry("idade", "reprovado")
                .containsEntry("titularidade", "analise_manual")
                .containsEntry("blacklist", "aprovado");

        assertThat(request.justificativa())
                .isEqualTo("Solicitante menor de 18 anos");

        assertThat(request.justificativa())
                .doesNotContain("Titularidade divergente")
                .doesNotContain("CPF não consta na blacklist");
    }

    private ResultadoRegra resultado(
            TipoVerificacao tipo,
            StatusVerificacao status,
            String motivo
    ) {
        return new ResultadoRegra(
                tipo,
                status,
                motivo
        );
    }

    private SolicitacaoResponse criarSolicitacao() {

        return new SolicitacaoResponse(
                "SOL-2026-001",
                "PF",
                "432.108.765-09",
                "Carlos Eduardo Mendes",
                LocalDate.of(1985, 3, 12),
                "5511987654321",
                "proprio",
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