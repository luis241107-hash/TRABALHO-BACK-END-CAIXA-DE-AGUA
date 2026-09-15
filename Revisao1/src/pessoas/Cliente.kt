package pessoas

import java.math.BigDecimal

class Cliente(
    id: Int? = null,
    nomeCliente: String,
    cpfCliente: String,
    idadeCliente: Int,
    var dividasAbertas: BigDecimal = BigDecimal.ZERO
) : Pessoa(id, nomeCliente, cpfCliente, idadeCliente)

/*21. (GENERALIZADAS) CLIENTE X FORNECEDOR
*
* CLIENTE POSSUI UMA CLASSE PROPRIA E HERDA DE PESSOA. FORNECEDOR NA VERSAÕ ATUAL
* NÃO POSSUI UMA CLASSE PRÓPRIA ELE É ARMAZENADO COMO TEXTO NA COMPRA
* COMO MELHORARIA: CRIARIA FORNECEDOR.KT HERDANDO DE PESSOA
* */