package repositorio

import pessoas.Instalador
import java.math.BigDecimal

class CRUDInstalador : interfaceJPA<Instalador>, ConexaoPostgres() {

    override fun salvar(item: Instalador) {
        require(item.nome.isNotBlank()) { "Nome é obrigatório." }
        require(item.cpf.matches(Regex("\\d{11}"))) { "CPF deve conter 11 números." }
        require(item.idade in 16..100) { "Idade inválida." }
        require(item.salario > BigDecimal.ZERO) { "Salário deve ser maior que zero." }
        require(item.setorId != null) { "O setor do funcionário é obrigatório." }

        comConexao { conn ->
            conn.autoCommit = false
            try {
                val pessoaId = conn.prepareStatement(
                    "INSERT INTO pessoa(nome, documento, idade, tipo) VALUES (?, ?, ?, 'FUNCIONARIO') RETURNING id"
                ).use { stmt ->
                    stmt.setString(1, item.nome)
                    stmt.setString(2, item.cpf)
                    stmt.setInt(3, item.idade)
                    stmt.executeQuery().use { rs -> rs.next(); rs.getInt(1) }
                }
                conn.prepareStatement(
                    "INSERT INTO instalador(pessoa_id, salario, turno, habilidade, setor_id) VALUES (?, ?, ?, ?, ?)"
                ).use { stmt ->
                    stmt.setInt(1, pessoaId)
                    stmt.setBigDecimal(2, item.salario)
                    stmt.setString(3, item.turno.name)
                    stmt.setString(4, item.habilidade.name)
                    stmt.setInt(5, item.setorId!!)
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
        println("Funcionário cadastrado com sucesso.")
    }

    override fun listar() {
        comConexao { conn ->
            conn.prepareStatement(
                """
                SELECT p.id, p.nome, p.documento, p.idade, i.salario, i.turno,
                       i.habilidade, s.nome AS setor
                FROM pessoa p
                JOIN instalador i ON i.pessoa_id=p.id
                JOIN setor s ON s.id=i.setor_id
                ORDER BY p.nome
                """.trimIndent()
            ).use { stmt ->
                stmt.executeQuery().use { rs ->
                    println("\n--- FUNCIONÁRIOS ---")
                    var encontrou = false
                    while (rs.next()) {
                        encontrou = true
                        println(
                            "ID ${rs.getInt("id")} | ${rs.getString("nome")} | CPF ${rs.getString("documento")} | " +
                            "salário R$ ${rs.getBigDecimal("salario")} | turno=${rs.getString("turno")} | " +
                            "habilidade=${rs.getString("habilidade")} | setor=${rs.getString("setor")}"
                        )
                    }
                    if (!encontrou) println("Nenhum funcionário cadastrado.")
                }
            }
        }
    }

    override fun editar(item: Instalador, id: Int) {
        comConexao { conn ->
            conn.prepareStatement(
                """
                UPDATE pessoa p SET nome=?, documento=?, idade=?
                FROM instalador i
                WHERE p.id=i.pessoa_id AND p.id=?
                """.trimIndent()
            ).use { stmt ->
                stmt.setString(1, item.nome)
                stmt.setString(2, item.cpf)
                stmt.setInt(3, item.idade)
                stmt.setInt(4, id)
                if (stmt.executeUpdate() == 0) throw IllegalArgumentException("Funcionário não encontrado.")
            }
            conn.prepareStatement(
                "UPDATE instalador SET salario=?, turno=?, habilidade=?, setor_id=? WHERE pessoa_id=?"
            ).use { stmt ->
                stmt.setBigDecimal(1, item.salario)
                stmt.setString(2, item.turno.name)
                stmt.setString(3, item.habilidade.name)
                stmt.setInt(4, item.setorId!!)
                stmt.setInt(5, id)
                stmt.executeUpdate()
            }
        }
        println("Funcionário atualizado com sucesso.")
    }

    override fun excluir(id: Int) {
        comConexao { conn ->
            conn.prepareStatement("DELETE FROM pessoa WHERE id=? AND tipo='FUNCIONARIO'").use { stmt ->
                stmt.setInt(1, id)
                if (stmt.executeUpdate() == 0) throw IllegalArgumentException("Funcionário não encontrado.")
            }
        }
        println("Funcionário excluído com sucesso.")
    }

    fun buscarNome(id: Int): String? = comConexao { conn ->
        conn.prepareStatement(
            "SELECT p.nome FROM pessoa p JOIN instalador i ON i.pessoa_id=p.id WHERE p.id=?"
        ).use { stmt ->
            stmt.setInt(1, id)
            stmt.executeQuery().use { rs -> if (rs.next()) rs.getString(1) else null }
        }
    }
}


/*19. (GENERALIZADAS) FUNCIONARIO E SETOR
*
* O FUNCIONARIO POSSUI UM setor_id QUE APONTA PARA A TABELA SETOR
* */

/*20. (GENEERALIZADAS) FUNCIONARIO MUDA DE SETOR
*
* NÃO PRECISO CRIAR UM NOVO FUNCIONARIO, APENAS ATUALIZO O SETOR_ID DAQUELE FUNCIONARIO
* */

/*26. (GENERALIZADAS) USO DO !!
*
* NA LINHA 97 POR EXEMPLO, O USO DAS EXCLAMAÇÕES AFIRMA PARA O KOTLIN QUE O VALOR NÃO É NULO,
* É ARRISCADO PORQUE SE ESTIVER NULO PODE CAUSAR NullPointerException.
* */

/*27. (GENERALIZADAS) USO DO ?: ? E !!
*
* ?. -> SE EXISTIR CONTINUA; ?: -> SE FOR NULO USA OUTRA OPÇÃO; !! -> GARANTO QUE NÃO É NULO
* */