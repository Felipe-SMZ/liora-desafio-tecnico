package com.liora.creditagent.client.dto;

import java.time.LocalDate;

public record SolicitacaoResponse(
        String solicitacaoId,
        String tipoPessoa,
        String cpfCnpj,
        String nomeSolicitante,
        LocalDate dataNascimento,
        String telefone,
        String tipoImovel,
        String uc,
        String distribuidora,
        String enderecoLogradouro,
        String enderecoNumero,
        String enderecoBairro,
        String enderecoCidade,
        String enderecoUf,
        String enderecoCep,
        String contaLuzTitular,
        String contaLuzTitularCpfCnpj,
        LocalDate contaLuzEmissao,
        Boolean contratoLocacaoVigente,
        LocalDate contratoLocacaoVencimento,
        String vinculoEmpresa) {
}
