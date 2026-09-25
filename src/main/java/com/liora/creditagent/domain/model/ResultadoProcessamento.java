package com.liora.creditagent.domain.model;

public record ResultadoProcessamento(
        int encontradas,
        int processadas,
        int falhas
) {
}
