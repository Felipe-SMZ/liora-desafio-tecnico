package com.liora.creditagent.domain.decision.rule;

import com.liora.creditagent.client.dto.TelefoneValidacaoResponse;
import com.liora.creditagent.domain.model.StatusVerificacao;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RegraTelefoneTest {

    private final RegraTelefone regraTelefone = new RegraTelefone(
            30,
            80
    );

    @Test
    void deveAprovarTelefoneNaoVoipComScoreBaixo() {

        var response = new TelefoneValidacaoResponse(
                "11999999999",
                false,
                0
        );
        var resultado = regraTelefone.avaliar(response);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.APROVADO);
    }

    @Test
    void deveSolicitarAnaliseManualTelefoneNaoVoipComScoreMedio() {

        var response = new TelefoneValidacaoResponse(
                "11999999999",
                false,
                50
        );
        var resultado = regraTelefone.avaliar(response);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.ANALISE_MANUAL);
    }

    @Test
    void deveReprovarTelefoneNaoVoipComScoreAlto() {

        var response = new TelefoneValidacaoResponse(
                "11999999999",
                false,
                100
        );
        var resultado = regraTelefone.avaliar(response);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.REPROVADO);
    }

    @Test
    void deveSolicitarAnaliseManualTelefoneVoipComScoreBaixo() {
        var response = new TelefoneValidacaoResponse(
                "11999999999",
                true,
                0
        );
        var resultado = regraTelefone.avaliar(response);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.ANALISE_MANUAL);
    }

    @Test
    void deveSolicitarAnaliseManualTelefoneVoipComScoreMedio() {
        var response = new TelefoneValidacaoResponse(
                "11999999999",
                true,
                50
        );
        var resultado = regraTelefone.avaliar(response);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.ANALISE_MANUAL);
    }

    @Test
    void deveReprovarTelefoneVoipComScoreAlto() {
        var response = new TelefoneValidacaoResponse(
                "11999999999",
                true,
                100
        );
        var resultado = regraTelefone.avaliar(response);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.REPROVADO);
    }

    @Test
    void deveSolicitarAnaliseManualQuandoScoreForIgualAoLimiteDeAprovacao() {

        var response = new TelefoneValidacaoResponse(
                "11999999999",
                false,
                30
        );

        var resultado = regraTelefone.avaliar(response);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.ANALISE_MANUAL);
    }

    @Test
    void deveReprovarQuandoScoreForIgualAoLimiteDeReprovacao() {

        var response = new TelefoneValidacaoResponse(
                "11999999999",
                false,
                80
        );

        var resultado = regraTelefone.avaliar(response);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.REPROVADO);
    }
}