package sistema.vendas

import repositorio.CRUDCaixaDaAgua
import repositorio.CRUDCliente
import repositorio.CRUDInstalador
import repositorio.CRUDVenda
import sistema.escolherOpcao
import sistema.lerInt
import sistema.lerTexto

fun menuVendas() {
    val crudVenda = CRUDVenda()
    while (true) {
        println(
            """

            --- VENDAS ---
            0 - VOLTAR
            1 - VENDER CAIXA D'ÁGUA
            2 - LISTAR VENDAS
            """.trimIndent()
        )
        when (lerInt("Opção: ")) {
            0 -> return
            1 -> {
                println("\nClientes cadastrados:")
                CRUDCliente().listar()
                val cliente = lerInt("ID do cliente: ", 1)

                println("\nProdutos disponíveis:")
                CRUDCaixaDaAgua().listar()
                val produto = lerInt("ID do produto: ", 1)
                val quantidade = lerInt("Quantidade: ", 1)

                println("\nFuncionários cadastrados:")
                CRUDInstalador().listar()
                val responsavel = lerInt("ID do responsável: ", 1)

                val forma = escolherOpcao(
                    "Forma de pagamento",
                    listOf("DINHEIRO", "PIX", "CARTAO", "BOLETO")
                ).let { listOf("DINHEIRO", "PIX", "CARTAO", "BOLETO")[it - 1] }

                crudVenda.venderProduto(cliente, produto, quantidade, responsavel, forma)
            }
            2 -> crudVenda.listar()
            else -> println("Opção inválida.")
        }
    }
}

/*15 (GENERALIZADAS) RESPONSAVEL PELA TRANSIÇÃO
*
* O REESPONSAVEL É ARMAZENADO PELO responsavel_id. NO MENU O USUARIO
* ESCOLHE O FUNCIONARIO RESPONSAVEL, E ESSE ID É ENVIADO PARA O CRUD
* */