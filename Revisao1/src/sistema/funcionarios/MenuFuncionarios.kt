package sistema.funcionarios

import enumeradores.Habilidade
import enumeradores.Turno
import pessoas.Instalador
import repositorio.CRUDInstalador
import sistema.escolherOpcao
import sistema.lerCpf
import sistema.lerDecimal
import sistema.lerInt
import sistema.lerTexto

fun menuFuncionarios() {
    val crud = CRUDInstalador()
    while (true) {
        println(
            """

            --- FUNCIONÁRIOS ---
            0 - VOLTAR
            1 - CADASTRAR
            2 - LISTAR
            3 - EDITAR
            4 - EXCLUIR
            """.trimIndent()
        )
        when (lerInt("Opção: ")) {
            0 -> return
            1 -> {
                val setor = escolherOpcao("Setor", listOf("ADMINISTRATIVO", "FINANCEIRO", "LOGÍSTICA"))
                val turno = escolherOpcao("Turno", Turno.entries.map { it.name })
                    .let { Turno.entries[it - 1] }
                val habilidade = escolherOpcao("Habilidade", Habilidade.entries.map { it.name })
                    .let { Habilidade.entries[it - 1] }

                crud.salvar(
                    Instalador(
                        nome = lerTexto("Nome: "),
                        cpf = lerCpf(),
                        idade = lerInt("Idade: ", 16, 100),
                        salario = lerDecimal("Salário: "),
                        turno = turno,
                        habilidade = habilidade,
                        setorId = setor
                    )
                )
            }
            2 -> crud.listar()
            3 -> {
                crud.listar()
                val id = lerInt("ID do funcionário: ", 1)
                val setor = escolherOpcao("Novo setor", listOf("ADMINISTRATIVO", "FINANCEIRO", "LOGÍSTICA"))
                val turno = escolherOpcao("Novo turno", Turno.entries.map { it.name })
                    .let { Turno.entries[it - 1] }
                val habilidade = escolherOpcao("Nova habilidade", Habilidade.entries.map { it.name })
                    .let { Habilidade.entries[it - 1] }
                crud.editar(
                    Instalador(
                        nome = lerTexto("Novo nome: "),
                        cpf = lerCpf(),
                        idade = lerInt("Nova idade: ", 16, 100),
                        salario = lerDecimal("Novo salário: "),
                        turno = turno,
                        habilidade = habilidade,
                        setorId = setor
                    ), id
                )
            }
            4 -> {
                crud.listar()
                crud.excluir(lerInt("ID do funcionário: ", 1))
            }
            else -> println("Opção inválida.")
        }
    }
}
