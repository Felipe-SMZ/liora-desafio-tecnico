package com.liora.creditagent.runner;

import com.liora.creditagent.service.ProcessamentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public ProcessamentoRunner(
            ProcessamentoService processamentoService
    ) {
        this.processamentoService = processamentoService;
    }

    @Override
    public void run(String... args) {

        log.info("Iniciando processamento das solicitações");

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