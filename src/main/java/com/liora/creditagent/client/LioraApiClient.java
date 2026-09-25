package com.liora.creditagent.client;

import com.liora.creditagent.client.dto.BlacklistResponse;
import com.liora.creditagent.client.dto.DebitosResponse;
import com.liora.creditagent.client.dto.EnderecoValidacaoResponse;
import com.liora.creditagent.client.dto.SolicitacaoResponse;
import com.liora.creditagent.client.dto.SolicitacoesResponse;
import com.liora.creditagent.client.dto.TelefoneValidacaoResponse;
import com.liora.creditagent.client.exception.ServicoTemporariamenteIndisponivelException;
import org.springframework.http.HttpStatus;
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

    public DebitosResponse consultarDebitos(String uc) {

        return restClient
                .get()
                .uri("/instalacao/{uc}/debitos", uc)
                .retrieve()
                .onStatus(
                        status -> status == HttpStatus.SERVICE_UNAVAILABLE,
                        (request, response) -> {
                            throw new ServicoTemporariamenteIndisponivelException(
                                    "Serviço de débitos temporariamente indisponível",
                                    null
                            );
                        }
                )
                .body(DebitosResponse.class);
    }
}