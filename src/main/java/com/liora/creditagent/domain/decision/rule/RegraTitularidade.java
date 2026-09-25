package com.liora.creditagent.domain.decision.rule;

import com.liora.creditagent.client.dto.SolicitacaoResponse;
import com.liora.creditagent.domain.model.ResultadoRegra;
import com.liora.creditagent.domain.model.StatusVerificacao;
import com.liora.creditagent.domain.model.TipoVerificacao;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RegraTitularidade implements RegraDecisao<SolicitacaoResponse> {

    @Override
    public ResultadoRegra avaliar(SolicitacaoResponse dados) {

        if (Objects.equals(
                dados.cpfCnpj(),
                dados.contaLuzTitularCpfCnpj()
        )) {
            return new ResultadoRegra(
                    TipoVerificacao.TITULARIDADE,
                    StatusVerificacao.APROVADO,
                    "Solicitante é o titular da conta de luz"
            );
        }

        if ("PF".equalsIgnoreCase(dados.tipoPessoa())
                && Boolean.TRUE.equals(dados.contratoLocacaoVigente())) {

            return new ResultadoRegra(
                    TipoVerificacao.TITULARIDADE,
                    StatusVerificacao.ANALISE_MANUAL,
                    "Titularidade divergente com contrato de locação vigente"
            );
        }

        if ("PJ".equalsIgnoreCase(dados.tipoPessoa())
                && "socio".equalsIgnoreCase(dados.vinculoEmpresa())) {

            return new ResultadoRegra(
                    TipoVerificacao.TITULARIDADE,
                    StatusVerificacao.ANALISE_MANUAL,
                    "Titularidade divergente com vínculo societário"
            );
        }

        return new ResultadoRegra(
                TipoVerificacao.TITULARIDADE,
                StatusVerificacao.REPROVADO,
                "Titularidade divergente sem justificativa"
        );
    }
}