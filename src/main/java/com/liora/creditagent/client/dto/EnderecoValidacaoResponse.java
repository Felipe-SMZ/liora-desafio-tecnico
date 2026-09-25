package com.liora.creditagent.client.dto;

public record EnderecoValidacaoResponse(
        boolean valido,
        Boolean cepConsistente,
        String logradouroNormalizado,
        String bairro,
        String municipio,
        String uf,
        String motivo,
        String cepCorretoSugerido
) {
}
