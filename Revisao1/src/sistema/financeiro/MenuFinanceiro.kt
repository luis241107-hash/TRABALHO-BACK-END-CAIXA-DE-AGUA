package sistema.financeiro

import repositorio.CRUDMovimentacao
import sistema.lerInt

fun menuFinanceiro() {
    val crud = CRUDMovimentacao()
    while (true) {
        println(
            """

            --- FINANCEIRO ---
            0 - VOLTAR
            1 - VER SALDO
            2 - LISTAR MOVIMENTAÇÕES
            """.trimIndent()
        )
        when (lerInt("Opção: ")) {
            0 -> return
            1 -> println("Saldo atual: R$ ${crud.saldo()}")
            2 -> crud.listar()
            else -> println("Opção inválida.")
        }
    }
}
