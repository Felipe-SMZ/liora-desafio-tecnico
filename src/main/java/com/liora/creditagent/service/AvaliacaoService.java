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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AvaliacaoService {

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

        var telefone =
                apiClient.validarTelefone(
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

        var request =
                avaliacaoMapper.mapear(
                        solicitacao,
                        resultado
                );

        return apiClient.enviarAvaliacao(request);
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

        return regraBlacklist.avaliar(blacklist);
    }

    private ResultadoRegra avaliarDebitos(
            SolicitacaoResponse solicitacao
    ) {

        try {

            var debitos =
                    apiClient.consultarDebitos(
                            solicitacao.uc()
                    );

            return regraDebitos.avaliar(debitos);

        } catch (
                ServicoTemporariamenteIndisponivelException e
        ) {

            return new ResultadoRegra(
                    TipoVerificacao.DEBITOS_INSTALACAO,
                    StatusVerificacao.ANALISE_MANUAL,
                    "Não foi possível consultar os débitos da instalação"
            );
        }
    }
}