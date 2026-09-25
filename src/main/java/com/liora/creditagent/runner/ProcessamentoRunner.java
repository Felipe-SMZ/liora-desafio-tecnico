package com.liora.creditagent.runner;

import com.liora.creditagent.client.LioraApiClient;
import com.liora.creditagent.service.AvaliacaoService;
import com.liora.creditagent.service.ProcessamentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "liora.processamento.executar",
        havingValue = "true"
)
public class ProcessamentoRunner implements CommandLineRunner {

    private static final Logger log =
            LoggerFactory.getLogger(ProcessamentoRunner.class);

    private final ProcessamentoService processamentoService;
    private final AvaliacaoService avaliacaoService;
    private final LioraApiClient apiClient;
    private final String solicitacaoId;

    public ProcessamentoRunner(
            ProcessamentoService processamentoService,
            AvaliacaoService avaliacaoService,
            LioraApiClient apiClient,
            @Value("${liora.processamento.solicitacao-id:}")
            String solicitacaoId
    ) {
        this.processamentoService = processamentoService;
        this.avaliacaoService = avaliacaoService;
        this.apiClient = apiClient;
        this.solicitacaoId = solicitacaoId;
    }

    @Override
    public void run(String... args) {

        if (solicitacaoId != null
                && !solicitacaoId.isBlank()) {

            processarUmaSolicitacao();
            return;
        }

        processarTodas();
    }

    private void processarUmaSolicitacao() {

        log.info(
                "Iniciando processamento da solicitação {}",
                solicitacaoId
        );

        var solicitacao =
                apiClient.buscarSolicitacaoPorId(
                        solicitacaoId
                );

        var resposta =
                avaliacaoService.processarSolicitacao(
                        solicitacao
                );

        log.info(
                "Solicitação {} processada. Avaliação: {}, status: {}",
                solicitacaoId,
                resposta.avaliacaoId(),
                resposta.status()
        );
    }

    private void processarTodas() {

        log.info(
                "Iniciando processamento de todas as solicitações"
        );

        var resultado =
                processamentoService.processarTodas();

        log.info(
                "Processamento concluído. Encontradas: {}, processadas: {}, falhas: {}",
                resultado.encontradas(),
                resultado.processadas(),
                resultado.falhas()
        );
    }
}