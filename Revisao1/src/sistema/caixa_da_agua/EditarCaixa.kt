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

fun editarCaixa() {
    val crud = CRUDCaixaDaAgua()
    crud.listar()

    val id = lerInt("Digite o ID da caixa que deseja alterar: ", 1)
    val marca = lerTexto("Nova marca: ")
    val modelo = lerTexto("Novo modelo: ")
    val largura = lerDecimal("Nova largura (m): ")
    val altura = lerDecimal("Nova altura (m): ")
    val profundidade = lerDecimal("Nova profundidade (m): ")

    val cor = escolherOpcao("Nova cor", Cor.entries.map { it.name.replace("_", " ") })
        .let { Cor.entries[it - 1] }
    val material = escolherOpcao("Novo material", Material.entries.map { it.name.replace("_", " ") })
        .let { Material.entries[it - 1] }
    val formato = escolherOpcao("Novo formato", Formato.entries.map { it.name.replace("_", " ") })
        .let { Formato.entries[it - 1].name }

    val preco = lerDecimal("Novo preço: ")

    crud.editar(
        CaixaDaAgua(
            marca = marca,
            modelo = modelo,
            dimensao = mutableListOf(largura.toDouble(), altura.toDouble(), profundidade.toDouble()),
            cor = cor,
            material = material,
            formato = formato,
            preco = preco
        ),
        id
    )
}
