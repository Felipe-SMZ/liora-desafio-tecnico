package com.liora.creditagent.client;

import com.liora.creditagent.client.dto.SolicitacaoResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class LioraApiClient {

    private final RestClient restClient;

    public LioraApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public SolicitacaoResponse buscarSolicitacaoPorId(String solicitacaoId) {

        return restClient
                .get()
                .uri("/solicitacoes/{id}", solicitacaoId)
                .retrieve()
                .body(SolicitacaoResponse.class);
    }
}