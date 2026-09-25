package com.liora.creditagent.domain.decision.rule;

import com.liora.creditagent.client.dto.BlacklistResponse;
import com.liora.creditagent.domain.model.ResultadoRegra;
import com.liora.creditagent.domain.model.StatusVerificacao;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegraBlacklistTest {

    private final RegraBlacklist regra = new RegraBlacklist();

    @Test
    void deveReprovarQuandoCpfEstiverNaBlacklist() {

        var response = new BlacklistResponse(
                "000.000.000-00",
                true,
                "Fraude documental confirmada"
        );

        ResultadoRegra resultado = regra.avaliar(response);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.REPROVADO);
    }

    @Test
    void deveAprovarQuandoCpfNaoEstiverNaBlacklist() {

        var response = new BlacklistResponse(
                "111.111.111-11",
                false,
                null
        );

        ResultadoRegra resultado = regra.avaliar(response);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.APROVADO);
    }
}