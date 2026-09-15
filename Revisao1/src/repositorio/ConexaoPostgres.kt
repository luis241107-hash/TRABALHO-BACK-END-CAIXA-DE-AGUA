package repositorio

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

open class ConexaoPostgres(
    private val user: String = System.getenv("POSTGRES_USER") ?: "postgres",
    private val senha: String = System.getenv("POSTGRES_PASSWORD") ?: "postgres",
    private val url: String = System.getenv("POSTGRES_URL")
        ?: "jdbc:postgresql://localhost:5433/caixaDaAgua"
) {
    protected var c: Connection? = null

    fun conectar() {
        if (c?.isClosed == false) return
        try {
            Class.forName("org.postgresql.Driver")
            c = DriverManager.getConnection(url, user, senha)
        } catch (e: SQLException) {
            throw IllegalStateException(
                "Não foi possível conectar ao PostgreSQL. Verifique banco, usuário, senha e driver JDBC.",
                e
            )
        } catch (e: ClassNotFoundException) {
            throw IllegalStateException(
                "Driver PostgreSQL não encontrado. Adicione o JDBC do PostgreSQL às bibliotecas do projeto.",
                e
            )
        }
    }

    fun desconectar() {
        try { c?.close() } finally { c = null }
    }

    protected fun <T> comConexao(acao: (Connection) -> T): T {
        conectar()
        return try {
            acao(requireNotNull(c))
        } finally {
            desconectar()
        }
    }
}

/*8.(GENERALIZADAS) CONEXÃO POSTGRES COMO ACONTECE
*
* ELA FICA CENTRALIZADA NESSA CLASSE, CARREGA O DRIVER PostgreSQL e usa o
* DriverManager.getConnection() pra conectar.
* */

/*9 (GENRALIZADAS) ALTERAÇÃO POSTGRES PARA MYSQL
*
* EU TERIA QUE ALTERAR A CLASSE DE CONEXAO E ADAPTAR O SCRUOT SQL E ALGUNS COMANDOS
* ESPECIFICOS DO POSTGRESQL. A CONEXAO ESTA CENTRALIZADA, OQUE FACILITARIA A TROCA
* MAS NÃO SERIA APENAS TROCA UMA LINHA
* */