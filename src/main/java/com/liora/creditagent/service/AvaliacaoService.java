package com.liora.creditagent.service;

import com.liora.creditagent.client.LioraApiClient;
import com.liora.creditagent.client.dto.AvaliacaoResponse;
import com.liora.creditagent.client.dto.SolicitacaoResponse;
import com.liora.creditagent.client.exception.ServicoTemporariamenteIndisponivelException;
import com.liora.creditagent.domain.decision.DecisionEngine;
import com.liora.creditagent.domain.decision.rule.*;
import com.liora.creditagent.domain.model.ResultadoDecisao;
import com.liora.creditagent.domain.model.ResultadoRegra;
import com.liora.creditagent.domain.model.StatusVerificacao;
import com.liora.creditagent.domain.model.TipoVerificacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AvaliacaoService {

    private static final Logger log =
            LoggerFactory.getLogger(AvaliacaoService.class);

    private static final int MAX_TENTATIVAS_DEBITOS = 2;

    private final LioraApiClient apiClient;
    private final RegraIdade regraIdade;
    private final RegraTitularidade regraTitularidade;
    private final RegraBlacklist regraBlacklist;
    private final RegraEndereco regraEndereco;
    private final RegraTelefone regraTelefone;
    private final RegraDebitos regraDebitos;
    private final DecisionEngine decisionEngine;
    private final AvaliacaoMapper avaliacaoMapper;

    public AvaliacaoService(
            LioraApiClient apiClient,
            RegraIdade regraIdade,
            RegraTitularidade regraTitularidade,
            RegraBlacklist regraBlacklist,
            RegraEndereco regraEndereco,
            RegraTelefone regraTelefone,
            RegraDebitos regraDebitos,
            DecisionEngine decisionEngine,
            AvaliacaoMapper avaliacaoMapper
    ) {
        this.apiClient = apiClient;
        this.regraIdade = regraIdade;
        this.regraTitularidade = regraTitularidade;
        this.regraBlacklist = regraBlacklist;
        this.regraEndereco = regraEndereco;
        this.regraTelefone = regraTelefone;
        this.regraDebitos = regraDebitos;
        this.decisionEngine = decisionEngine;
        this.avaliacaoMapper = avaliacaoMapper;
    }

    public ResultadoDecisao avaliarSolicitacao(
            SolicitacaoResponse solicitacao
    ) {

        List<ResultadoRegra> verificacoes = new ArrayList<>();

        verificacoes.add(
                regraIdade.avaliar(solicitacao)
        );

        verificacoes.add(
                regraTitularidade.avaliar(solicitacao)
        );

        verificacoes.add(
                avaliarBlacklist(solicitacao)
        );

        var endereco = apiClient.validarEndereco(
                solicitacao.enderecoCep(),
                solicitacao.enderecoLogradouro(),
                solicitacao.enderecoCidade(),
                solicitacao.enderecoUf()
        );

        verificacoes.add(
                regraEndereco.avaliar(endereco)
        );

        var telefone = apiClient.validarTelefone(
                solicitacao.telefone()
        );

        verificacoes.add(
                regraTelefone.avaliar(telefone)
        );

        verificacoes.add(
                avaliarDebitos(solicitacao)
        );

        return decisionEngine.decidir(verificacoes);
    }

    public AvaliacaoResponse processarSolicitacao(
            SolicitacaoResponse solicitacao
    ) {

        ResultadoDecisao resultado =
                avaliarSolicitacao(solicitacao);

        log.info(
                "Solicitação {} avaliada com decisão {}",
                solicitacao.solicitacaoId(),
                resultado.decisao()
        );

        var avaliacaoRequest =
                avaliacaoMapper.mapear(
                        solicitacao,
                        resultado
                );

        var resposta =
                apiClient.enviarAvaliacao(
                        avaliacaoRequest
                );

        log.info(
                "Avaliação da solicitação {} enviada com sucesso. Avaliação: {}, status: {}",
                solicitacao.solicitacaoId(),
                resposta.avaliacaoId(),
                resposta.status()
        );

        return resposta;
    }

    private ResultadoRegra avaliarBlacklist(
            SolicitacaoResponse solicitacao
    ) {

        if (!"PF".equalsIgnoreCase(
                solicitacao.tipoPessoa()
        )) {

            return new ResultadoRegra(
                    TipoVerificacao.BLACKLIST,
                    StatusVerificacao.APROVADO,
                    "Verificação de blacklist de CPF não aplicável à pessoa jurídica"
            );
        }

        var blacklist =
                apiClient.consultarBlacklist(
                        solicitacao.cpfCnpj()
                );

        return regraBlacklist.avaliar(
                blacklist
        );
    }

    private ResultadoRegra avaliarDebitos(
            SolicitacaoResponse solicitacao
    ) {

        for (
                int tentativa = 1;
                tentativa <= MAX_TENTATIVAS_DEBITOS;
                tentativa++
        ) {

            try {

                var debitos =
                        apiClient.consultarDebitos(
                                solicitacao.uc()
                        );

                return regraDebitos.avaliar(
                        debitos
                );

            } catch (
                    ServicoTemporariamenteIndisponivelException e
            ) {

                log.warn(
                        "Serviço de débitos indisponível para solicitação {}. Tentativa {}/{}",
                        solicitacao.solicitacaoId(),
                        tentativa,
                        MAX_TENTATIVAS_DEBITOS
                );

                if (tentativa == MAX_TENTATIVAS_DEBITOS) {

                    log.warn(
                            "Consulta de débitos falhou em todas as tentativas para solicitação {}. Encaminhando para análise manual",
                            solicitacao.solicitacaoId()
                    );

                    return new ResultadoRegra(
                            TipoVerificacao.DEBITOS_INSTALACAO,
                            StatusVerificacao.ANALISE_MANUAL,
                            "Serviço de débitos indisponível após "
                                    + MAX_TENTATIVAS_DEBITOS
                                    + " tentativas"
                    );
                }
            }
        }

        throw new IllegalStateException(
                "Fluxo inesperado na consulta de débitos"
        );
    }
}