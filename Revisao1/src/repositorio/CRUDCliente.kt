package repositorio

import pessoas.Cliente
import java.math.BigDecimal

class CRUDCliente : interfaceJPA<Cliente>, ConexaoPostgres() {

    override fun salvar(item: Cliente) {
        require(item.nome.isNotBlank()) { "Nome é obrigatório." }
        require(item.cpf.matches(Regex("\\d{11}"))) { "CPF deve conter 11 números." }
        require(item.idade in 0..130) { "Idade inválida." }

        comConexao { conn ->
            conn.autoCommit = false
            try {
                val pessoaId = conn.prepareStatement(
                    "INSERT INTO pessoa(nome, documento, idade, tipo) VALUES (?, ?, ?, 'CLIENTE') RETURNING id"
                ).use { stmt ->
                    stmt.setString(1, item.nome)
                    stmt.setString(2, item.cpf)
                    stmt.setInt(3, item.idade)
                    stmt.executeQuery().use { rs -> rs.next(); rs.getInt(1) }
                }
                conn.prepareStatement("INSERT INTO cliente(pessoa_id, dividas_abertas) VALUES (?, ?)").use { stmt ->
                    stmt.setInt(1, pessoaId)
                    stmt.setBigDecimal(2, item.dividasAbertas)
                    stmt.executeUpdate()
                }
                conn.commit()
            } catch (e: Exception) {
                conn.rollback()
                throw e
            } finally {
                conn.autoCommit = true
            }
        }
        println("Cliente cadastrado com sucesso.")
    }

    override fun listar() {
        comConexao { conn ->
            conn.prepareStatement(
                """
                SELECT p.id, p.nome, p.documento, p.idade, c.dividas_abertas
                FROM pessoa p JOIN cliente c ON c.pessoa_id=p.id
                ORDER BY p.nome
                """.trimIndent()
            ).use { stmt ->
                stmt.executeQuery().use { rs ->
                    println("\n--- CLIENTES ---")
                    var encontrou = false
                    while (rs.next()) {
                        encontrou = true
                        println(
                            "ID ${rs.getInt("id")} | ${rs.getString("nome")} | CPF ${rs.getString("documento")} | " +
                            "idade ${rs.getInt("idade")} | dívidas R$ ${rs.getBigDecimal("dividas_abertas")}"
                        )
                    }
                    if (!encontrou) println("Nenhum cliente cadastrado.")
                }
            }
        }
    }

    override fun editar(item: Cliente, id: Int) {
        comConexao { conn ->
            conn.prepareStatement(
                "UPDATE pessoa SET nome=?, documento=?, idade=? WHERE id=? AND tipo='CLIENTE'"
            ).use { stmt ->
                stmt.setString(1, item.nome)
                stmt.setString(2, item.cpf)
                stmt.setInt(3, item.idade)
                stmt.setInt(4, id)
                if (stmt.executeUpdate() == 0) throw IllegalArgumentException("Cliente não encontrado.")
            }
            conn.prepareStatement("UPDATE cliente SET dividas_abertas=? WHERE pessoa_id=?").use { stmt ->
                stmt.setBigDecimal(1, item.dividasAbertas)
                stmt.setInt(2, id)
                stmt.executeUpdate()
            }
        }
        println("Cliente atualizado com sucesso.")
    }

    override fun excluir(id: Int) {
        comConexao { conn ->
            conn.prepareStatement("DELETE FROM pessoa WHERE id=? AND tipo='CLIENTE'").use { stmt ->
                stmt.setInt(1, id)
                if (stmt.executeUpdate() == 0) throw IllegalArgumentException("Cliente não encontrado.")
            }
        }
        println("Cliente excluído com sucesso.")
    }

    fun buscarNome(id: Int): String? = comConexao { conn ->
        conn.prepareStatement(
            "SELECT p.nome FROM pessoa p JOIN cliente c ON c.pessoa_id=p.id WHERE p.id=?"
        ).use { stmt ->
            stmt.setInt(1, id)
            stmt.executeQuery().use { rs -> if (rs.next()) rs.getString(1) else null }
        }
    }
}
