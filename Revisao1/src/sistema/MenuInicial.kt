package sistema

import sistema.caixa_da_agua.cadastrarNovaCaixa
import sistema.caixa_da_agua.editarCaixa
import sistema.caixa_da_agua.excluirCaixa
import sistema.caixa_da_agua.listarCaixa
import sistema.caixa_da_agua.entradaEstoque
import sistema.clientes.menuClientes
import sistema.financeiro.menuFinanceiro
import sistema.funcionarios.menuFuncionarios
import sistema.pagamentos.pagar
import sistema.vendas.menuVendas
import java.math.BigDecimal

fun lerTexto(mensagem: String, obrigatorio: Boolean = true): String {
    while (true) {
        print(mensagem)
        val valor = readlnOrNull()?.trim()
        if (!obrigatorio || !valor.isNullOrBlank()) return valor.orEmpty()
        println("Valor obrigatório. Tente novamente.")
    }
}

fun lerInt(mensagem: String, minimo: Int? = null, maximo: Int? = null): Int {
    while (true) {
        print(mensagem)
        val valor = readlnOrNull()?.trim()?.toIntOrNull()
        if (valor != null &&
            (minimo == null || valor >= minimo) &&
            (maximo == null || valor <= maximo)
        ) return valor
        println("Número inválido. Tente novamente.")
    }
}

fun lerDecimal(mensagem: String, positivo: Boolean = true): BigDecimal {
    while (true) {
        print(mensagem)
        val entrada = readlnOrNull()?.trim()?.replace(",", ".")
        val valor = entrada?.toBigDecimalOrNull()
        if (valor != null && (!positivo || valor > BigDecimal.ZERO)) return valor
        println("Valor monetário inválido.")
    }
}

fun lerCpf(): String {
    while (true) {
        val entrada = lerTexto("CPF (somente números): ").replace(Regex("\\D"), "")
        if (entrada.matches(Regex("\\d{11}"))) return entrada
        println("CPF inválido. Informe exatamente 11 números.")
    }
}

fun escolherOpcao(titulo: String, opcoes: List<String>): Int {
    println("\n$titulo")
    opcoes.forEachIndexed { indice, opcao -> println("${indice + 1} - $opcao") }
    return lerInt("Opção: ", 1, opcoes.size)
}

fun menuInicial() {
    while (true) {
        println(
            """

            ==========================================
                    SISTEMA CAIXA D'ÁGUA
            ==========================================
            0 - SAIR
            1 - CADASTRAR CAIXA DE ÁGUA
            2 - EDITAR CAIXA DE ÁGUA
            3 - LISTAR CAIXA DE ÁGUA
            4 - EXCLUIR CAIXA DE ÁGUA
            5 - ENTRADA DE ESTOQUE / COMPRA
            6 - CLIENTES
            7 - FUNCIONÁRIOS
            8 - FINANCEIRO
            9 - PAGAMENTOS
            10 - VENDAS
            ==========================================
            """.trimIndent()
        )

        try {
            when (lerInt("Opção: ")) {
                0 -> {
                    println("Programa encerrado.")
                    return
                }
                1 -> cadastrarNovaCaixa()
                2 -> editarCaixa()
                3 -> listarCaixa()
                4 -> excluirCaixa()
                5 -> entradaEstoque()
                6 -> menuClientes()
                7 -> menuFuncionarios()
                8 -> menuFinanceiro()
                9 -> pagar()
                10 -> menuVendas()
                else -> println("Opção inválida.")
            }
        } catch (e: Exception) {
            println("\nNão foi possível concluir a operação: ${e.message ?: "erro desconhecido"}")
        }
    }
}


/*10. (GENERALIZADAS) ORGANIZAÇÃO DOS PACKAGES
*
* NÃO EXISTE UMA DEPENDENCIA CIRCULAR IMPORTABNT, EU SEPAREI AS RSPONSABILIDADES PARA QUE O
* MENU CHAME O REPOSITORIO E ELE MESMO CUIDE DO ACESSO AO BANCO
* */

/*22. (GENERALIZADAS) REGEX LINHA 49
*
* ESSA REGE VERIFICA SE O CPF POSSUI EXATAMENTE 11 NUMEROS
* */

/*23 (GENERALIZADAS) OQUE O REGEX ACEITA
*
* A REGEX DA LINHA 49 VERIFICA O FORMATO, NÃO SE O CPF MATEMATICAMENTE VERDADEIRO
* */

/*24. (GENERALIZADAS) POR QUE REGEX?
*
* PORQUE NÃO BASTA VERIFICAR A QUANTIDADE DOS CARACTERES, TAMBÉM É NECESSARIO
* QUE TODOS SJAM NUMEROS
* */

/*28 (GENERALIZADAS) USUARIO APERTA ENTER EM CAMPO OBRIGATORIO
*
*LINHA 19 SE O USUARIO APERTAR ENTER SEM DIGITAR NADA O SISTEMA DETECTA QUE O CAMPO ESTÁ
* VAZIO E PEDE NOVAMENTE, A INFORMAÇÃO INVALIDA NEM CHEGA AO BANCO
* */

/*29. (GENERALIZADAS) ONDE AINDA PODE QUEBRAR?
*
* NO REGEX ACHO QUE AINDA PODE QUEBRAR, PORQUE ELE NÃO VERIFICA SE O CPF EXISTE OU SE ESTA CERTO
* ELE APENAS VERIFICA SE ESTA DENTRO DA QUANTIDADE DE NUMEROS E ETC. UMA MELHORIA SERIA IMPLEMENTAR
* O CALCULO DOS DIGITOS VERIFICADORES DO CPF
* */