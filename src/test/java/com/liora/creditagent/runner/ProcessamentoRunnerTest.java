package com.liora.creditagent.runner;

import com.liora.creditagent.client.LioraApiClient;
import com.liora.creditagent.client.dto.AvaliacaoResponse;
import com.liora.creditagent.client.dto.SolicitacaoResponse;
import com.liora.creditagent.domain.model.ResultadoProcessamento;
import com.liora.creditagent.service.AvaliacaoService;
import com.liora.creditagent.service.ProcessamentoService;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

class ProcessamentoRunnerTest {

    @Test
    void deveProcessarSomenteUmaSolicitacaoQuandoIdForInformado() {

        var processamentoService = mock(ProcessamentoService.class);
        var avaliacaoService = mock(AvaliacaoService.class);
        var apiClient = mock(LioraApiClient.class);

        var solicitacao = mock(SolicitacaoResponse.class);

        var resposta = new AvaliacaoResponse(
                UUID.randomUUID(),
                "salvo"
        );

        when(apiClient.buscarSolicitacaoPorId("SOL-2026-001"))
                .thenReturn(solicitacao);

        when(avaliacaoService.processarSolicitacao(solicitacao))
                .thenReturn(resposta);

        var runner = new ProcessamentoRunner(
                processamentoService,
                avaliacaoService,
                apiClient,
                "SOL-2026-001"
        );

        runner.run();

        verify(apiClient)
                .buscarSolicitacaoPorId("SOL-2026-001");

        verify(avaliacaoService)
                .processarSolicitacao(solicitacao);

        verifyNoInteractions(processamentoService);
    }

    @Test
    void deveProcessarTodasQuandoIdNaoForInformado() {

        var processamentoService = mock(ProcessamentoService.class);
        var avaliacaoService = mock(AvaliacaoService.class);
        var apiClient = mock(LioraApiClient.class);

        when(processamentoService.processarTodas())
                .thenReturn(
                        new ResultadoProcessamento(
                                10,
                                10,
                                0
                        )
                );

        var runner = new ProcessamentoRunner(
                processamentoService,
                avaliacaoService,
                apiClient,
                ""
        );

        runner.run();

        verify(processamentoService)
                .processarTodas();

        verifyNoInteractions(apiClient);
        verifyNoInteractions(avaliacaoService);
    }
}