package repositorio

interface interfaceJPA<T> {
    fun salvar(item: T)
    fun listar()
    fun editar(item: T, id: Int)
    fun excluir(id: Int)
}


/*2.(GENERALIZADAS):

* USEI INTERFACE PARA PADRONIZAR AS OPERAÇÕES CRUD, FAZENDO COM QUE OS REPOSITORIOS
* TENHAM OS MESMOS METODOS BASICOS
* */

