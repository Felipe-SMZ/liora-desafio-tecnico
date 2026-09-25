package com.liora.creditagent.domain.decision.rule;

import com.liora.creditagent.client.dto.DebitosResponse;
import com.liora.creditagent.domain.model.StatusVerificacao;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RegraDebitosTest {

    private final RegraDebitos regraDebitos = new RegraDebitos();

    @Test
    void deveAprovarQuandoInstalacaoEstiverRegular() {

        var response = new DebitosResponse(
                "3001234567",
                "regular",
                BigDecimal.ZERO,
                0,
                false,
                false
        );

        var resultado = regraDebitos.avaliar(response);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.APROVADO);
    }

    @Test
    void deveSolicitarAnaliseManualQuandoHouverDebitosPendentes() {

        var response = new DebitosResponse(
                "3001234567",
                "inadimplente",
                new BigDecimal("350.50"),
                2,
                true,
                false
        );

        var resultado = regraDebitos.avaliar(response);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.ANALISE_MANUAL);
    }

    @Test
    void deveReprovarQuandoHouverCorteProgramado() {

        var response = new DebitosResponse(
                "3001234567",
                "inadimplente",
                new BigDecimal("800.00"),
                4,
                true,
                true
        );

        var resultado = regraDebitos.avaliar(response);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.REPROVADO);
    }

    @Test
    void deveAprovarQuandoHouverApenasHistoricoDeInadimplencia() {

        var response = new DebitosResponse(
                "3001234567",
                "regular",
                BigDecimal.ZERO,
                0,
                true,
                false
        );

        var resultado = regraDebitos.avaliar(response);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.APROVADO);
    }
}
