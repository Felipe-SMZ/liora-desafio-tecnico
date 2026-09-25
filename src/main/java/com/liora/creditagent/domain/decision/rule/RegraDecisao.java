package com.liora.creditagent.domain.decision.rule;

import com.liora.creditagent.domain.model.ResultadoRegra;

public interface RegraDecisao<T> {
    ResultadoRegra avaliar(T dados);
}
