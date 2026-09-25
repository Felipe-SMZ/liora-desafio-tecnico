package com.liora.creditagent.service;

import com.liora.creditagent.client.LioraApiClient;
import com.liora.creditagent.client.dto.PaginationResponse;
import com.liora.creditagent.client.dto.SolicitacaoResponse;
import com.liora.creditagent.client.dto.SolicitacoesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessamentoServiceTest {

    @Mock
    private LioraApiClient apiClient;

    @Mock
    private AvaliacaoService avaliacaoService;

    private ProcessamentoService service;

    @BeforeEach
    void setUp() {
        service = new ProcessamentoService(
                apiClient,
                avaliacaoService
        );
    }

    @Test
    void deveProcessarTodasAsSolicitacoesDeUmaPagina() {

        var solicitacao1 =
                criarSolicitacao("SOL-2026-001");

        var solicitacao2 =
                criarSolicitacao("SOL-2026-002");

        var pagina = new SolicitacoesResponse(
                List.of(
                        solicitacao1,
                        solicitacao2
                ),
                new PaginationResponse(
                        2,
                        200,
                        0
                )
        );

        when(apiClient.buscarSolicitacoes(200, 0))
                .thenReturn(pagina);

        var resultado =
                service.processarTodas();

        assertThat(resultado.encontradas())
                .isEqualTo(2);

        assertThat(resultado.processadas())
                .isEqualTo(2);

        assertThat(resultado.falhas())
                .isZero();

        verify(avaliacaoService)
                .processarSolicitacao(solicitacao1);

        verify(avaliacaoService)
                .processarSolicitacao(solicitacao2);

        verify(
                apiClient,
                times(1)
        ).buscarSolicitacoes(200, 0);
    }

    @Test
    void devePercorrerMaisDeUmaPagina() {

        var solicitacao1 =
                criarSolicitacao("SOL-2026-001");

        var solicitacao2 =
                criarSolicitacao("SOL-2026-002");

        var solicitacao3 =
                criarSolicitacao("SOL-2026-003");

        var primeiraPagina =
                new SolicitacoesResponse(
                        List.of(
                                solicitacao1,
                                solicitacao2
                        ),
                        new PaginationResponse(
                                3,
                                2,
                                0
                        )
                );

        var segundaPagina =
                new SolicitacoesResponse(
                        List.of(
                                solicitacao3
                        ),
                        new PaginationResponse(
                                3,
                                2,
                                2
                        )
                );

        when(apiClient.buscarSolicitacoes(200, 0))
                .thenReturn(primeiraPagina);

        when(apiClient.buscarSolicitacoes(200, 2))
                .thenReturn(segundaPagina);

        var resultado =
                service.processarTodas();

        assertThat(resultado.encontradas())
                .isEqualTo(3);

        assertThat(resultado.processadas())
                .isEqualTo(3);

        assertThat(resultado.falhas())
                .isZero();

        verify(avaliacaoService)
                .processarSolicitacao(solicitacao1);

        verify(avaliacaoService)
                .processarSolicitacao(solicitacao2);

        verify(avaliacaoService)
                .processarSolicitacao(solicitacao3);

        verify(apiClient)
                .buscarSolicitacoes(200, 0);

        verify(apiClient)
                .buscarSolicitacoes(200, 2);

        verify(
                apiClient,
                never()
        ).buscarSolicitacoes(200, 4);
    }

    @Test
    void deveContinuarProcessamentoQuandoUmaSolicitacaoFalhar() {

        var solicitacao1 =
                criarSolicitacao("SOL-2026-001");

        var solicitacao2 =
                criarSolicitacao("SOL-2026-002");

        var pagina =
                new SolicitacoesResponse(
                        List.of(
                                solicitacao1,
                                solicitacao2
                        ),
                        new PaginationResponse(
                                2,
                                200,
                                0
                        )
                );

        when(apiClient.buscarSolicitacoes(200, 0))
                .thenReturn(pagina);

        doThrow(
                new RuntimeException(
                        "Erro ao processar solicitação"
                )
        )
                .when(avaliacaoService)
                .processarSolicitacao(solicitacao1);

        var resultado =
                service.processarTodas();

        assertThat(resultado.encontradas())
                .isEqualTo(2);

        assertThat(resultado.processadas())
                .isEqualTo(1);

        assertThat(resultado.falhas())
                .isEqualTo(1);

        verify(avaliacaoService)
                .processarSolicitacao(solicitacao1);

        verify(avaliacaoService)
                .processarSolicitacao(solicitacao2);
    }

    private SolicitacaoResponse criarSolicitacao(
            String solicitacaoId
    ) {

        return new SolicitacaoResponse(
                solicitacaoId,
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