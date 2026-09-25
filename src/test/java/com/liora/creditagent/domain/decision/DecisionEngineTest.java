package com.liora.creditagent.domain.decision;

import com.liora.creditagent.domain.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class DecisionEngineTest {

    private final DecisionEngine decisionEngine = new DecisionEngine();

    @Test
    void deveAprovarQuandoTodasAsVerificacoesForemAprovadas() {

        var verificacoes = List.of(
                resultado(TipoVerificacao.IDADE, StatusVerificacao.APROVADO),
                resultado(TipoVerificacao.TITULARIDADE, StatusVerificacao.APROVADO),
                resultado(TipoVerificacao.BLACKLIST, StatusVerificacao.APROVADO),
                resultado(TipoVerificacao.ENDERECO, StatusVerificacao.APROVADO),
                resultado(TipoVerificacao.TELEFONE, StatusVerificacao.APROVADO),
                resultado(TipoVerificacao.DEBITOS_INSTALACAO, StatusVerificacao.APROVADO)
        );

        var resultado = decisionEngine.decidir(verificacoes);

        assertThat(resultado.decisao())
                .isEqualTo(Decisao.APROVADO);
    }

    @Test
    void deveSolicitarAnaliseManualQuandoAlgumaVerificacaoExigirAnaliseManual() {

        var verificacoes = List.of(
                resultado(TipoVerificacao.IDADE, StatusVerificacao.APROVADO),
                resultado(TipoVerificacao.ENDERECO, StatusVerificacao.ANALISE_MANUAL),
                resultado(TipoVerificacao.BLACKLIST, StatusVerificacao.APROVADO)
        );

        var resultado = decisionEngine.decidir(verificacoes);

        assertThat(resultado.decisao())
                .isEqualTo(Decisao.ANALISE_MANUAL);
    }

    @Test
    void deveReprovarQuandoAlgumaVerificacaoForReprovada() {

        var verificacoes = List.of(
                resultado(TipoVerificacao.IDADE, StatusVerificacao.APROVADO),
                resultado(TipoVerificacao.BLACKLIST, StatusVerificacao.REPROVADO),
                resultado(TipoVerificacao.ENDERECO, StatusVerificacao.APROVADO)
        );

        var resultado = decisionEngine.decidir(verificacoes);

        assertThat(resultado.decisao())
                .isEqualTo(Decisao.REPROVADO);
    }

    @Test
    void devePriorizarReprovacaoSobreAnaliseManual() {

        var verificacoes = List.of(
                resultado(TipoVerificacao.IDADE, StatusVerificacao.REPROVADO),
                resultado(TipoVerificacao.TITULARIDADE, StatusVerificacao.ANALISE_MANUAL),
                resultado(TipoVerificacao.BLACKLIST, StatusVerificacao.APROVADO)
        );

        var resultado = decisionEngine.decidir(verificacoes);

        assertThat(resultado.decisao())
                .isEqualTo(Decisao.REPROVADO);
    }

    @Test
    void deveLancarExcecaoQuandoListaDeVerificacoesEstiverVazia() {

        assertThatThrownBy(() -> decisionEngine.decidir(List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveLancarExcecaoQuandoListaDeVerificacoesForNula() {

        assertThatThrownBy(() -> decisionEngine.decidir(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private ResultadoRegra resultado(
            TipoVerificacao tipo,
            StatusVerificacao status
    ) {
        return new ResultadoRegra(
                tipo,
                status,
                "Motivo de teste"
        );
    }
}