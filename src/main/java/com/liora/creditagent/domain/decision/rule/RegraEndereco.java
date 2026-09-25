package com.liora.creditagent.domain.decision.rule;

import com.liora.creditagent.client.dto.EnderecoValidacaoResponse;
import com.liora.creditagent.domain.model.ResultadoRegra;
import com.liora.creditagent.domain.model.StatusVerificacao;
import com.liora.creditagent.domain.model.TipoVerificacao;

public class RegraEndereco implements RegraDecisao<EnderecoValidacaoResponse> {

    @Override
    public ResultadoRegra avaliar(EnderecoValidacaoResponse dados) {

        if (dados.valido()) {
            return new ResultadoRegra(
                    TipoVerificacao.ENDERECO,
                    StatusVerificacao.APROVADO,
                    "Endereço validado com sucesso");
        }

        String cepSugerido = dados.cepCorretoSugerido();

        if (cepSugerido == null || cepSugerido.isBlank()) {
            return new ResultadoRegra(
                    TipoVerificacao.ENDERECO,
                    StatusVerificacao.REPROVADO,
                    "Endereço inválido");
        }

        return new ResultadoRegra(
                TipoVerificacao.ENDERECO,
                StatusVerificacao.ANALISE_MANUAL,
                "Endereço inválido. CEP sugerido: " + cepSugerido);
    }

}
