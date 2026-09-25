package com.liora.creditagent.client;

import com.liora.creditagent.client.dto.*;
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

    public SolicitacoesResponse buscarSolicitacoes(int limit, int offset) {

        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/solicitacoes")
                        .queryParam("limit", limit)
                        .queryParam("offset", offset)
                        .build())
                .retrieve()
                .body(SolicitacoesResponse.class);
    }

    public BlacklistResponse consultarBlacklist(String cpf) {

        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/cpf/blacklist")
                        .queryParam("cpf", cpf)
                        .build())
                .retrieve()
                .body(BlacklistResponse.class);
    }

    public TelefoneValidacaoResponse validarTelefone(String telefone) {

        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/telefone/validar")
                        .queryParam("telefone", telefone)
                        .build())
                .retrieve()
                .body(TelefoneValidacaoResponse.class);
    }

    public EnderecoValidacaoResponse validarEndereco(
            String cep,
            String logradouro,
            String cidade,
            String uf
    ) {

        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/endereco/validar")
                        .queryParam("cep", cep)
                        .queryParam("logradouro", logradouro)
                        .queryParam("cidade", cidade)
                        .queryParam("uf", uf)
                        .build())
                .retrieve()
                .body(EnderecoValidacaoResponse.class);
    }
}