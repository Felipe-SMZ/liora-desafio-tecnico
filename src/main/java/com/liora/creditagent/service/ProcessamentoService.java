package com.liora.creditagent.service;

import com.liora.creditagent.client.LioraApiClient;
import com.liora.creditagent.domain.model.ResultadoProcessamento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProcessamentoService {

    private static final Logger log =
            LoggerFactory.getLogger(ProcessamentoService.class);

    private static final int TAMANHO_PAGINA = 200;

    private final LioraApiClient apiClient;
    private final AvaliacaoService avaliacaoService;

    public ProcessamentoService(
            LioraApiClient apiClient,
            AvaliacaoService avaliacaoService
    ) {
        this.apiClient = apiClient;
        this.avaliacaoService = avaliacaoService;
    }

    public ResultadoProcessamento processarTodas() {

        int offset = 0;
        int encontradas = 0;
        int processadas = 0;
        int falhas = 0;

        while (true) {

            var pagina =
                    apiClient.buscarSolicitacoes(
                            TAMANHO_PAGINA,
                            offset
                    );

            if (pagina == null
                    || pagina.solicitacoes() == null
                    || pagina.solicitacoes().isEmpty()) {

                break;
            }

            encontradas += pagina.solicitacoes().size();

            for (var solicitacao : pagina.solicitacoes()) {

                try {

                    avaliacaoService.processarSolicitacao(
                            solicitacao
                    );

                    processadas++;

                } catch (Exception e) {

                    falhas++;

                    log.error(
                            "Erro ao processar solicitação {}",
                            solicitacao.solicitacaoId(),
                            e
                    );
                }
            }

            int proximoOffset =
                    pagina.pagination().offset()
                            + pagina.pagination().limit();

            if (proximoOffset
                    >= pagina.pagination().total()) {

                break;
            }

            offset = proximoOffset;
        }

        return new ResultadoProcessamento(
                encontradas,
                processadas,
                falhas
        );
    }
}