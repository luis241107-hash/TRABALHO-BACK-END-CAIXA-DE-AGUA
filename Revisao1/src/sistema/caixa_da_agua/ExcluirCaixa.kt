package sistema.caixa_da_agua

import repositorio.CRUDCaixaDaAgua
import sistema.lerInt

fun excluirCaixa() {
    val crud = CRUDCaixaDaAgua()
    crud.listar()
    val id = lerInt("Digite o ID que deseja excluir: ", 1)
    crud.excluir(id)
}
