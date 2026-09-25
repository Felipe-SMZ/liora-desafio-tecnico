package com.liora.creditagent.client;

import com.liora.creditagent.client.exception.ServicoTemporariamenteIndisponivelException;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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

    @Test
    void deveBuscarSolicitacoesPaginadas() throws InterruptedException {

        String responseBody = """
                {
                  "solicitacoes": [
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
                  ],
                  "pagination": {
                    "total": 999,
                    "limit": 200,
                    "offset": 0
                  }
                }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .addHeader("Content-Type", "application/json")
                        .setBody(responseBody)
        );

        var response = client.buscarSolicitacoes(200, 0);

        assertThat(response).isNotNull();

        assertThat(response.solicitacoes())
                .hasSize(1);

        assertThat(response.solicitacoes().getFirst().solicitacaoId())
                .isEqualTo("SOL-2026-001");

        assertThat(response.pagination().total())
                .isEqualTo(999);

        assertThat(response.pagination().limit())
                .isEqualTo(200);

        assertThat(response.pagination().offset())
                .isZero();

        var request = mockWebServer.takeRequest();

        assertThat(request.getMethod())
                .isEqualTo("GET");

        assertThat(request.getPath())
                .isEqualTo(
                        "/api/public/v1/solicitacoes?limit=200&offset=0"
                );

        assertThat(request.getHeader("Authorization"))
                .isEqualTo("Bearer token-teste");
    }

    @Test
    void deveConsultarBlacklistDoCpf() throws InterruptedException {

        String responseBody = """
                {
                  "cpf": "432.108.765-09",
                  "blacklist": false,
                  "motivo": null
                }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .addHeader("Content-Type", "application/json")
                        .setBody(responseBody)
        );

        var response =
                client.consultarBlacklist("432.108.765-09");

        assertThat(response).isNotNull();

        assertThat(response.cpf())
                .isEqualTo("432.108.765-09");

        assertThat(response.blacklist())
                .isFalse();

        assertThat(response.motivo())
                .isNull();

        var request = mockWebServer.takeRequest();

        assertThat(request.getMethod())
                .isEqualTo("GET");

        assertThat(request.getPath())
                .isEqualTo(
                        "/api/public/v1/cpf/blacklist?cpf=432.108.765-09"
                );

        assertThat(request.getHeader("Authorization"))
                .isEqualTo("Bearer token-teste");
    }

    @Test
    void deveValidarTelefone() throws InterruptedException {

        String responseBody = """
                {
                  "telefone": "5511987654321",
                  "voip": false,
                  "fraude_score": 18
                }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .addHeader("Content-Type", "application/json")
                        .setBody(responseBody)
        );

        var response =
                client.validarTelefone("5511987654321");

        assertThat(response).isNotNull();

        assertThat(response.telefone())
                .isEqualTo("5511987654321");

        assertThat(response.voip())
                .isFalse();

        assertThat(response.fraudeScore())
                .isEqualTo(18);

        var request = mockWebServer.takeRequest();

        assertThat(request.getMethod())
                .isEqualTo("GET");

        assertThat(request.getPath())
                .isEqualTo(
                        "/api/public/v1/telefone/validar?telefone=5511987654321"
                );

        assertThat(request.getHeader("Authorization"))
                .isEqualTo("Bearer token-teste");
    }

    @Test
    void deveValidarEndereco() throws InterruptedException {

        String responseBody = """
                {
                  "valido": true,
                  "cep_consistente": true,
                  "logradouro_normalizado": "Rua das Acácias",
                  "bairro": "Jardim Paulista",
                  "municipio": "São Paulo",
                  "uf": "SP",
                  "motivo": null,
                  "cep_correto_sugerido": null
                }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .addHeader("Content-Type", "application/json")
                        .setBody(responseBody)
        );

        var response = client.validarEndereco(
                "01401-000",
                "Rua das Acácias",
                "São Paulo",
                "SP"
        );

        assertThat(response).isNotNull();

        assertThat(response.valido())
                .isTrue();

        assertThat(response.cepConsistente())
                .isTrue();

        assertThat(response.logradouroNormalizado())
                .isEqualTo("Rua das Acácias");

        assertThat(response.bairro())
                .isEqualTo("Jardim Paulista");

        assertThat(response.municipio())
                .isEqualTo("São Paulo");

        assertThat(response.uf())
                .isEqualTo("SP");

        assertThat(response.motivo())
                .isNull();

        assertThat(response.cepCorretoSugerido())
                .isNull();

        var request = mockWebServer.takeRequest();

        assertThat(request.getMethod())
                .isEqualTo("GET");

        assertThat(request.getPath())
                .isEqualTo(
                        "/api/public/v1/endereco/validar" +
                                "?cep=01401-000" +
                                "&logradouro=Rua%20das%20Ac%C3%A1cias" +
                                "&cidade=S%C3%A3o%20Paulo" +
                                "&uf=SP"
                );

        assertThat(request.getHeader("Authorization"))
                .isEqualTo("Bearer token-teste");
    }

    @Test
    void deveConsultarDebitosDaInstalacao()
            throws InterruptedException {

        String responseBody = """
                {
                  "uc": "3001234567",
                  "status": "regular",
                  "debitos_total": 0,
                  "faturas_em_atraso": 0,
                  "historico_inadimplencia": false,
                  "corte_programado": false
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
                client.consultarDebitos("3001234567");

        assertThat(response).isNotNull();

        assertThat(response.uc())
                .isEqualTo("3001234567");

        assertThat(response.status())
                .isEqualTo("regular");

        assertThat(response.debitosTotal())
                .isEqualByComparingTo("0");

        assertThat(response.faturasEmAtraso())
                .isZero();

        assertThat(response.historicoInadimplencia())
                .isFalse();

        assertThat(response.corteProgramado())
                .isFalse();

        var request = mockWebServer.takeRequest();

        assertThat(request.getMethod())
                .isEqualTo("GET");

        assertThat(request.getPath())
                .isEqualTo(
                        "/api/public/v1/instalacao/3001234567/debitos"
                );

        assertThat(request.getHeader("Authorization"))
                .isEqualTo("Bearer token-teste");
    }

    @Test
    void deveInformarIndisponibilidadeTemporariaAoConsultarDebitos()
            throws InterruptedException {

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(503)
                        .addHeader(
                                "Content-Type",
                                "application/json"
                        )
                        .setBody("""
                                {
                                  "error": "service_unavailable",
                                  "message": "Serviço temporariamente indisponível",
                                  "retry_after": 30
                                }
                                """)
        );

        assertThatThrownBy(
                () -> client.consultarDebitos("3001234569")
        )
                .isInstanceOf(
                        ServicoTemporariamenteIndisponivelException.class
                )
                .hasMessage(
                        "Serviço de débitos temporariamente indisponível"
                );

        var request = mockWebServer.takeRequest();

        assertThat(request.getMethod())
                .isEqualTo("GET");

        assertThat(request.getPath())
                .isEqualTo(
                        "/api/public/v1/instalacao/3001234569/debitos"
                );

        assertThat(request.getHeader("Authorization"))
                .isEqualTo("Bearer token-teste");
    }
}