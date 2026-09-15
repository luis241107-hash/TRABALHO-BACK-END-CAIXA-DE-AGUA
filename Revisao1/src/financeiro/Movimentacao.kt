package financeiro

import java.math.BigDecimal
import java.time.LocalDateTime

data class Movimentacao(
    val valor: BigDecimal,
    val dataMovimentacao: LocalDateTime,
    val contexto: String,
    val pagador: String? = null,
    val recebedor: String? = null,
    val responsavelId: Int? = null,
    val tipo: String = if (valor >= BigDecimal.ZERO) "RECEITA" else "DESPESA"
)
