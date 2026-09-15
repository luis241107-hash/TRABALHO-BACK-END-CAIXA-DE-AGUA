package financeiro

import java.math.BigDecimal

class Caixa private constructor(
    private var saldo: BigDecimal
) {
    fun getSaldo(): BigDecimal = saldo

    fun receita(valor: BigDecimal): BigDecimal {
        require(valor > BigDecimal.ZERO) { "A receita deve ser maior que zero." }
        saldo = saldo.add(valor)
        return saldo
    }

    fun despesa(valor: BigDecimal): BigDecimal {
        require(valor > BigDecimal.ZERO) { "A despesa deve ser maior que zero." }
        if (saldo < valor) throw IllegalStateException("Saldo insuficiente.")
        saldo = saldo.subtract(valor)
        return saldo
    }

    companion object {
        fun comSaldo(saldo: BigDecimal): Caixa = Caixa(saldo)
    }
}
/*4.(GENERALIZADAS) IMPEDIR SALDO NEGATIVO
O ATRIBUTO SALDO É PRIVATE, OU SEJA, NÃO PODE SER ALTERADO POR OUTRAS CLASSES DIRETAMENTE.
PARA RETIRAR O DINHEIRO É NECESSÁRIO USAR O despesa() QUE VERIFICA SE EXISTE SALDO
* */