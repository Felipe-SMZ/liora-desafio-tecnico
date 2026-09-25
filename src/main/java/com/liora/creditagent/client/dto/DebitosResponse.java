package com.liora.creditagent.client.dto;

import java.math.BigDecimal;

public record DebitosResponse(
        String uc,
        String status,
        BigDecimal debitosTotal,
        int faturasEmAtraso,
        boolean historicoInadimplencia,
        boolean corteProgramado
) {
}
