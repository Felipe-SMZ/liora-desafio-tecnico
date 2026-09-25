package com.liora.creditagent.service;

import com.liora.creditagent.client.LioraApiClient;
import com.liora.creditagent.client.dto.BlacklistResponse;
import com.liora.creditagent.client.dto.DebitosResponse;
import com.liora.creditagent.client.dto.EnderecoValidacaoResponse;
import com.liora.creditagent.client.dto.SolicitacaoResponse;
import com.liora.creditagent.client.dto.TelefoneValidacaoResponse;
import com.liora.creditagent.client.exception.ServicoTemporariamenteIndisponivelException;
import com.liora.creditagent.domain.decision.DecisionEngine;
import com.liora.creditagent.domain.decision.rule.RegraBlacklist;
import com.liora.creditagent.domain.decision.rule.RegraDebitos;
import com.liora.creditagent.domain.decision.rule.RegraEndereco;
import com.liora.creditagent.domain.decision.rule.RegraIdade;
import com.liora.creditagent.domain.decision.rule.RegraTelefone;
import com.liora.creditagent.domain.decision.rule.RegraTitularidade;
import com.liora.creditagent.domain.model.Decisao;
import com.liora.creditagent.domain.model.ResultadoRegra;
import com.liora.creditagent.domain.model.StatusVerificacao;
import com.liora.creditagent.domain.model.TipoVerificacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvaliacaoServiceTest {

    @Mock
    private LioraApiClient apiClient;

    @Mock
    private RegraIdade regraIdade;

    @Mock
    private RegraTitularidade regraTitularidade;

    @Mock
    private RegraBlacklist regraBlacklist;

    @Mock
    private RegraEndereco regraEndereco;

    @Mock
    private RegraTelefone regraTelefone;

    @Mock
    private RegraDebitos regraDebitos;

    @Mock
    private AvaliacaoMapper avaliacaoMapper;

    private AvaliacaoService service;

    @BeforeEach
    void setUp() {

        service = new AvaliacaoService(
                apiClient,
                regraIdade,
                regraTitularidade,
                regraBlacklist,
                regraEndereco,
                regraTelefone,
                regraDebitos,
                new DecisionEngine(),
                avaliacaoMapper
        );
    }

    @Test
    void deveAprovarQuandoTodasAsVerificacoesForemAprovadas() {

        var solicitacao = criarSolicitacao();

        var blacklist =
                new BlacklistResponse(
                        solicitacao.cpfCnpj(),
                        false,
                        null
                );

        var endereco =
                new EnderecoValidacaoResponse(
                        true,
                        true,
                        "Rua das Acácias",
                        "Jardim Paulista",
                        "São Paulo",
                        "SP",
                        null,
                        null
                );

        var telefone =
                new TelefoneValidacaoResponse(
                        solicitacao.telefone(),
                        false,
                        10
                );

        var debitos =
                new DebitosResponse(
                        solicitacao.uc(),
                        "regular",
                        BigDecimal.ZERO,
                        0,
                        false,
                        false
                );

        when(regraIdade.avaliar(solicitacao))
                .thenReturn(aprovado(TipoVerificacao.IDADE));

        when(regraTitularidade.avaliar(solicitacao))
                .thenReturn(aprovado(TipoVerificacao.TITULARIDADE));

        when(apiClient.consultarBlacklist(solicitacao.cpfCnpj()))
                .thenReturn(blacklist);

        when(regraBlacklist.avaliar(blacklist))
                .thenReturn(aprovado(TipoVerificacao.BLACKLIST));

        when(apiClient.validarEndereco(
                solicitacao.enderecoCep(),
                solicitacao.enderecoLogradouro(),
                solicitacao.enderecoCidade(),
                solicitacao.enderecoUf()
        )).thenReturn(endereco);

        when(regraEndereco.avaliar(endereco))
                .thenReturn(aprovado(TipoVerificacao.ENDERECO));

        when(apiClient.validarTelefone(
                solicitacao.telefone()
        )).thenReturn(telefone);

        when(regraTelefone.avaliar(telefone))
                .thenReturn(aprovado(TipoVerificacao.TELEFONE));

        when(apiClient.consultarDebitos(
                solicitacao.uc()
        )).thenReturn(debitos);

        when(regraDebitos.avaliar(debitos))
                .thenReturn(
                        aprovado(
                                TipoVerificacao.DEBITOS_INSTALACAO
                        )
                );

        var resultado =
                service.avaliarSolicitacao(solicitacao);

        assertThat(resultado.decisao())
                .isEqualTo(Decisao.APROVADO);

        assertThat(resultado.verificacoes())
                .hasSize(6);

        assertThat(resultado.verificacoes())
                .allMatch(
                        verificacao ->
                                verificacao.status()
                                        == StatusVerificacao.APROVADO
                );
    }

    @Test
    void deveEnviarParaAnaliseManualQuandoConsultaDeDebitosEstiverIndisponivel() {

        var solicitacao = criarSolicitacao();

        var blacklist =
                new BlacklistResponse(
                        solicitacao.cpfCnpj(),
                        false,
                        null
                );

        var endereco =
                new EnderecoValidacaoResponse(
                        true,
                        true,
                        "Rua das Acácias",
                        "Jardim Paulista",
                        "São Paulo",
                        "SP",
                        null,
                        null
                );

        var telefone =
                new TelefoneValidacaoResponse(
                        solicitacao.telefone(),
                        false,
                        10
                );

        when(regraIdade.avaliar(solicitacao))
                .thenReturn(aprovado(TipoVerificacao.IDADE));

        when(regraTitularidade.avaliar(solicitacao))
                .thenReturn(aprovado(TipoVerificacao.TITULARIDADE));

        when(apiClient.consultarBlacklist(solicitacao.cpfCnpj()))
                .thenReturn(blacklist);

        when(regraBlacklist.avaliar(blacklist))
                .thenReturn(aprovado(TipoVerificacao.BLACKLIST));

        when(apiClient.validarEndereco(
                solicitacao.enderecoCep(),
                solicitacao.enderecoLogradouro(),
                solicitacao.enderecoCidade(),
                solicitacao.enderecoUf()
        )).thenReturn(endereco);

        when(regraEndereco.avaliar(endereco))
                .thenReturn(aprovado(TipoVerificacao.ENDERECO));

        when(apiClient.validarTelefone(
                solicitacao.telefone()
        )).thenReturn(telefone);

        when(regraTelefone.avaliar(telefone))
                .thenReturn(aprovado(TipoVerificacao.TELEFONE));

        when(apiClient.consultarDebitos(
                solicitacao.uc()
        )).thenThrow(
                new ServicoTemporariamenteIndisponivelException(
                        "Serviço indisponível",
                        null
                )
        );

        var resultado =
                service.avaliarSolicitacao(solicitacao);

        assertThat(resultado.decisao())
                .isEqualTo(Decisao.ANALISE_MANUAL);

        assertThat(resultado.verificacoes())
                .hasSize(6);

        assertThat(resultado.verificacoes())
                .anyMatch(
                        verificacao ->
                                verificacao.regra()
                                        == TipoVerificacao.DEBITOS_INSTALACAO
                                        &&
                                        verificacao.status()
                                                == StatusVerificacao.ANALISE_MANUAL
                );
    }

    private ResultadoRegra aprovado(
            TipoVerificacao tipo
    ) {

        return new ResultadoRegra(
                tipo,
                StatusVerificacao.APROVADO,
                "Verificação aprovada"
        );
    }

    private SolicitacaoResponse criarSolicitacao() {

        return new SolicitacaoResponse(
                "SOL-2026-001",
                "PF",
                "432.108.765-09",
                "Carlos Eduardo Mendes",
                LocalDate.of(1985, 3, 12),
                "5511987654321",
                "proprio",
                "3001234567",
                "ENEL SP",
                "Rua das Acácias",
                "250",
                "Jardim Paulista",
                "São Paulo",
                "SP",
                "01401-000",
                "Carlos Eduardo Mendes",
                "432.108.765-09",
                LocalDate.of(2026, 4, 15),
                null,
                null,
                null
        );
    }
}