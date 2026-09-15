package sistema.pagamentos

import repositorio.CRUDInstalador
import repositorio.CRUDMovimentacao
import sistema.lerDecimal
import sistema.lerInt
import sistema.lerTexto

fun pagar() {
    println("\n--- PAGAMENTO / DESPESA ---")
    val descricao = lerTexto("Descrição/motivo: ")
    val valor = lerDecimal("Valor: ")

    println("Funcionário responsável:")
    CRUDInstalador().listar()
    val responsavelId = lerInt("ID do responsável: ", 1)

    val pagador = "CAIXA DA EMPRESA"
    val recebedor = lerTexto("Quem recebeu: ")

    CRUDMovimentacao().salvar(
        valor = valor,
        tipo = "DESPESA",
        pagador = pagador,
        recebedor = recebedor,
        responsavelId = responsavelId,
        descricao = descricao
    )
    println("Pagamento registrado com sucesso.")
}
