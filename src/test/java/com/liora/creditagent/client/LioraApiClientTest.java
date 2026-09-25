package com.liora.creditagent.client;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LioraApiClientTest {

    private static MockWebServer mockWebServer;

    @Autowired
    private LioraApiClient client;

    @BeforeAll
    static void iniciarServidor() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void encerrarServidor() throws IOException {
        mockWebServer.shutdown();
    }

    @DynamicPropertySource
    static void configurarPropriedades(DynamicPropertyRegistry registry) {

        registry.add(
                "liora.api.base-url",
                () -> mockWebServer.url("/api/public/v1").toString()
        );

        registry.add(
                "liora.api.token",
                () -> "token-teste"
        );
    }

    @Test
    void deveBuscarSolicitacaoPorId() throws InterruptedException {

        String responseBody = """
                {
                  "solicitacao_id": "SOL-2026-001",
                  "tipo_pessoa": "PF",
                  "cpf_cnpj": "432.108.765-09",
                  "nome_solicitante": "Carlos Eduardo Mendes",
                  "data_nascimento": "1985-03-12",
                  "telefone": "5511987654321",
                  "tipo_imovel": "proprio",
                  "uc": "3001234567",
                  "distribuidora": "ENEL SP",
                  "endereco_logradouro": "Rua das Acácias",
                  "endereco_numero": "250",
                  "endereco_bairro": "Jardim Paulista",
                  "endereco_cidade": "São Paulo",
                  "endereco_uf": "SP",
                  "endereco_cep": "01401-000",
                  "conta_luz_titular": "Carlos Eduardo Mendes",
                  "conta_luz_titular_cpf_cnpj": "432.108.765-09",
                  "conta_luz_emissao": "2026-04-15",
                  "contrato_locacao_vigente": null,
                  "contrato_locacao_vencimento": null,
                  "vinculo_empresa": null
                }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .addHeader(
                                "Content-Type",
                                "application/json"
                        )
                        .setBody(responseBody)
        );

        var response =
                client.buscarSolicitacaoPorId("SOL-2026-001");

        assertThat(response)
                .isNotNull();

        assertThat(response.solicitacaoId())
                .isEqualTo("SOL-2026-001");

        assertThat(response.tipoPessoa())
                .isEqualTo("PF");

        assertThat(response.cpfCnpj())
                .isEqualTo("432.108.765-09");

        assertThat(response.nomeSolicitante())
                .isEqualTo("Carlos Eduardo Mendes");

        assertThat(response.dataNascimento())
                .isEqualTo(LocalDate.of(1985, 3, 12));

        assertThat(response.telefone())
                .isEqualTo("5511987654321");

        assertThat(response.tipoImovel())
                .isEqualTo("proprio");

        assertThat(response.uc())
                .isEqualTo("3001234567");

        assertThat(response.distribuidora())
                .isEqualTo("ENEL SP");

        assertThat(response.enderecoLogradouro())
                .isEqualTo("Rua das Acácias");

        assertThat(response.enderecoNumero())
                .isEqualTo("250");

        assertThat(response.enderecoBairro())
                .isEqualTo("Jardim Paulista");

        assertThat(response.enderecoCidade())
                .isEqualTo("São Paulo");

        assertThat(response.enderecoUf())
                .isEqualTo("SP");

        assertThat(response.enderecoCep())
                .isEqualTo("01401-000");

        assertThat(response.contaLuzTitular())
                .isEqualTo("Carlos Eduardo Mendes");

        assertThat(response.contaLuzTitularCpfCnpj())
                .isEqualTo("432.108.765-09");

        assertThat(response.contaLuzEmissao())
                .isEqualTo(LocalDate.of(2026, 4, 15));

        assertThat(response.contratoLocacaoVigente())
                .isNull();

        assertThat(response.contratoLocacaoVencimento())
                .isNull();

        assertThat(response.vinculoEmpresa())
                .isNull();

        var request = mockWebServer.takeRequest();

        assertThat(request.getMethod())
                .isEqualTo("GET");

        assertThat(request.getPath())
                .isEqualTo(
                        "/api/public/v1/solicitacoes/SOL-2026-001"
                );

        assertThat(request.getHeader("Authorization"))
                .isEqualTo("Bearer token-teste");
    }
}