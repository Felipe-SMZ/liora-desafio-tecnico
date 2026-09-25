package com.liora.creditagent.domain.decision.rule;

import com.liora.creditagent.client.dto.EnderecoValidacaoResponse;
import com.liora.creditagent.domain.model.ResultadoRegra;
import com.liora.creditagent.domain.model.StatusVerificacao;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RegraEnderecoTest {

    private final RegraEndereco regra = new RegraEndereco();

    @Test
    void deveAprovarQuandoEnderecoForValido() {

        var response = new EnderecoValidacaoResponse(
                true,
                true,
                "Rua Válida",
                "Jardim Válido",
                "São Paulo",
                "SP",
                null,
                null
        );

        ResultadoRegra resultado = regra.avaliar(response);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.APROVADO);
    }

    @Test
    void deveReprovarQuandoEnderecoForInvalidoSemCepSugerido() {

        var response = new EnderecoValidacaoResponse(
                false,
                null,
                null,
                null,
                null,
                null,
                "logradouro_nao_encontrado",
                null
        );

        ResultadoRegra resultado = regra.avaliar(response);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.REPROVADO);
    }

    @Test
    void deveReprovarQuandoCepSugeridoEstiverEmBranco() {

        var response = new EnderecoValidacaoResponse(
                false,
                null,
                null,
                null,
                null,
                null,
                "endereco_invalido",
                "   "
        );

        ResultadoRegra resultado = regra.avaliar(response);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.REPROVADO);
    }

    @Test
    void deveSolicitarAnaliseManualQuandoEnderecoForInvalidoMasTiverCepSugerido() {

        var response = new EnderecoValidacaoResponse(
                false,
                null,
                null,
                null,
                null,
                null,
                "logradouro_nao_encontrado",
                "12345-678"
        );

        ResultadoRegra resultado = regra.avaliar(response);

        assertThat(resultado.status())
                .isEqualTo(StatusVerificacao.ANALISE_MANUAL);
    }
}
