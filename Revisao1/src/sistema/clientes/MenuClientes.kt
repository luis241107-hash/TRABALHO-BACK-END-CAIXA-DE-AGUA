package sistema.clientes

import pessoas.Cliente
import repositorio.CRUDCliente
import sistema.lerCpf
import sistema.lerDecimal
import sistema.lerInt
import sistema.lerTexto

fun menuClientes() {
    val crud = CRUDCliente()
    while (true) {
        println(
            """

            --- CLIENTES ---
            0 - VOLTAR
            1 - CADASTRAR
            2 - LISTAR
            3 - EDITAR
            4 - EXCLUIR
            """.trimIndent()
        )
        when (lerInt("Opção: ")) {
            0 -> return
            1 -> crud.salvar(
                Cliente(
                    nomeCliente = lerTexto("Nome: "),
                    cpfCliente = lerCpf(),
                    idadeCliente = lerInt("Idade: ", 0, 130),
                    dividasAbertas = lerDecimal("Dívidas abertas (0 se nenhuma): ", positivo = false)
                )
            )
            2 -> crud.listar()
            3 -> {
                crud.listar()
                val id = lerInt("ID do cliente: ", 1)
                crud.editar(
                    Cliente(
                        nomeCliente = lerTexto("Novo nome: "),
                        cpfCliente = lerCpf(),
                        idadeCliente = lerInt("Nova idade: ", 0, 130),
                        dividasAbertas = lerDecimal("Novas dívidas abertas: ", positivo = false)
                    ), id
                )
            }
            4 -> {
                crud.listar()
                crud.excluir(lerInt("ID do cliente: ", 1))
            }
            else -> println("Opção inválida.")
        }
    }
}
