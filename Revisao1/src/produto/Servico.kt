package produto

import java.math.BigDecimal
import java.time.LocalDate

class Servico(
    val id: Int? = null,
    val clienteId: Int,
    val instaladorId: Int,
    val preco: BigDecimal,
    val dataInstalacao: LocalDate,
    val descricao: String,
    val status: String = "AGENDADO"
)
