package sistema.caixa_da_agua

import enumeradores.Cor
import enumeradores.Formato
import enumeradores.Material
import produto.CaixaDaAgua
import repositorio.CRUDCaixaDaAgua
import sistema.escolherOpcao
import sistema.lerDecimal
import sistema.lerInt
import sistema.lerTexto

fun cadastrarNovaCaixa() {
    println("\n--- CADASTRAR CAIXA D'ÁGUA ---")
    val marca = lerTexto("Digite a marca: ")
    val modelo = lerTexto("Digite o modelo: ")
    val largura = lerDecimal("Digite a largura (m): ")
    val altura = lerDecimal("Digite a altura (m): ")
    val profundidade = lerDecimal("Digite a profundidade (m): ")

    val cor = escolherOpcao(
        "Escolha a cor",
        Cor.entries.map { it.name.replace("_", " ") }
    ).let { Cor.entries[it - 1] }

    val material = escolherOpcao(
        "Escolha o material",
        Material.entries.map { it.name.replace("_", " ") }
    ).let { Material.entries[it - 1] }

    val formato = escolherOpcao(
        "Escolha o formato",
        Formato.entries.map { it.name.replace("_", " ") }
    ).let { Formato.entries[it - 1].name }

    val preco = lerDecimal("Digite o preço de venda: ")
    val estoque = lerInt("Estoque inicial: ", 0)

    CRUDCaixaDaAgua().salvar(
        CaixaDaAgua(
            marca = marca,
            modelo = modelo,
            dimensao = mutableListOf(largura.toDouble(), altura.toDouble(), profundidade.toDouble()),
            cor = cor,
            material = material,
            formato = formato,
            preco = preco,
            estoque = estoque
        )
    )
}


fun entradaEstoque() {
    val crudProduto = CRUDCaixaDaAgua()
    crudProduto.listar()

    val produtoId = lerInt("ID do produto comprado: ", 1)
    val quantidade = lerInt("Quantidade comprada: ", 1)
    val custo = lerDecimal("Custo unitário da compra: ")
    val fornecedor = lerTexto("Nome do fornecedor: ")

    println("Funcionários responsáveis:")
    repositorio.CRUDInstalador().listar()
    val responsavel = lerInt("ID do funcionário responsável: ", 1)

    repositorio.CRUDVenda().comprarEstoque(
        produtoId = produtoId,
        quantidade = quantidade,
        custoUnitario = custo,
        fornecedor = fornecedor,
        responsavelId = responsavel
    )
}
