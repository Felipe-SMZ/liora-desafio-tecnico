package com.liora.creditagent.service;

import com.liora.creditagent.client.dto.AvaliacaoRequest;
import com.liora.creditagent.client.dto.SolicitacaoResponse;
import com.liora.creditagent.domain.model.Decisao;
import com.liora.creditagent.domain.model.ResultadoDecisao;
import com.liora.creditagent.domain.model.ResultadoRegra;
import com.liora.creditagent.domain.model.StatusVerificacao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AvaliacaoMapper {

    private final String agenteVersao;

    public AvaliacaoMapper(
            @Value("${liora.agente.versao}") String agenteVersao
    ) {
        this.agenteVersao = agenteVersao;
    }

    public AvaliacaoRequest mapear(
            SolicitacaoResponse solicitacao,
            ResultadoDecisao resultado
    ) {

        Map<String, String> verificacoes = resultado.verificacoes()
                .stream()
                .collect(Collectors.toMap(
                        verificacao -> normalizar(
                                verificacao.regra().name()
                        ),
                        verificacao -> normalizar(
                                verificacao.status().name()
                        ),
                        (valorExistente, novoValor) -> novoValor,
                        LinkedHashMap::new
                ));

        String justificativa = construirJustificativa(resultado);

        return new AvaliacaoRequest(
                solicitacao.solicitacaoId(),
                solicitacao.cpfCnpj(),
                normalizar(resultado.decisao().name()),
                null,
                verificacoes,
                justificativa,
                agenteVersao
        );
    }

    private String construirJustificativa(ResultadoDecisao resultado) {

        if (resultado.decisao() == Decisao.APROVADO) {
            return "Todas as verificações foram aprovadas.";
        }

        StatusVerificacao statusRelevante =
                resultado.decisao() == Decisao.REPROVADO
                        ? StatusVerificacao.REPROVADO
                        : StatusVerificacao.ANALISE_MANUAL;

        return resultado.verificacoes()
                .stream()
                .filter(verificacao ->
                        verificacao.status() == statusRelevante)
                .map(ResultadoRegra::motivo)
                .collect(Collectors.joining("; "));
    }

    private String normalizar(String valor) {
        return valor.toLowerCase(Locale.ROOT);
    }
}