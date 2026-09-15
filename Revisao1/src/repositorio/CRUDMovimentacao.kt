package repositorio

import financeiro.Movimentacao
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDateTime

class CRUDMovimentacao : interfaceJPA<Movimentacao>, ConexaoPostgres() {

    override fun salvar(item: Movimentacao) {
        require(item.valor.compareTo(BigDecimal.ZERO) != 0) { "O valor não pode ser zero." }
        require(item.contexto.isNotBlank()) { "A descrição é obrigatória." }

        comConexao { conn ->
            conn.autoCommit = false
            try {
                conn.prepareStatement(
                    """
                    INSERT INTO movimentacao
                    (valor, data_hora, tipo, pagador, recebedor, responsavel_id, descricao)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """.trimIndent()
                ).use { stmt ->
                    stmt.setBigDecimal(1, item.valor.abs())
                    stmt.setTimestamp(2, Timestamp.valueOf(item.dataMovimentacao))
                    stmt.setString(3, if (item.valor >= BigDecimal.ZERO) "RECEITA" else "DESPESA")
                    stmt.setString(4, item.pagador)
                    stmt.setString(5, item.recebedor)
                    if (item.responsavelId == null) stmt.setNull(6, java.sql.Types.INTEGER)
                    else stmt.setInt(6, item.responsavelId)
                    stmt.setString(7, item.contexto)
                    stmt.executeUpdate()
                }

                conn.prepareStatement("UPDATE caixa SET saldo=saldo+? WHERE id=1").use { stmt ->
                    stmt.setBigDecimal(1, item.valor)
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
    }

    fun salvar(
        valor: BigDecimal,
        tipo: String,
        pagador: String?,
        recebedor: String?,
        responsavelId: Int?,
        descricao: String
    ) {
        val sinal = if (tipo.uppercase() == "DESPESA") -valor.abs() else valor.abs()
        salvar(
            Movimentacao(
                valor = sinal,
                dataMovimentacao = LocalDateTime.now(),
                contexto = descricao,
                pagador = pagador,
                recebedor = recebedor,
                responsavelId = responsavelId,
                tipo = tipo.uppercase()
            )
        )
    }

    override fun listar() {
        comConexao { conn ->
            conn.prepareStatement(
                """
                SELECT m.id, m.valor, m.data_hora, m.tipo, m.pagador, m.recebedor,
                       m.descricao, m.responsavel_id, COALESCE(p.nome, 'N/A') AS responsavel
                FROM movimentacao m
                LEFT JOIN instalador i ON i.pessoa_id=m.responsavel_id
                LEFT JOIN pessoa p ON p.id=i.pessoa_id
                ORDER BY m.data_hora DESC
                """.trimIndent()
            ).use { stmt ->
                stmt.executeQuery().use { rs ->
                    println("\n--- MOVIMENTAÇÕES ---")
                    var encontrou = false
                    while (rs.next()) {
                        encontrou = true
                        println(
                            "ID ${rs.getInt("id")} | ${rs.getString("tipo")} | R$ ${rs.getBigDecimal("valor")} | " +
                            "${rs.getTimestamp("data_hora")} | pagador=${rs.getString("pagador")} | " +
                            "recebedor=${rs.getString("recebedor")} | responsável=${rs.getString("responsavel")} | " +
                            rs.getString("descricao")
                        )
                    }
                    if (!encontrou) println("Nenhuma movimentação cadastrada.")
                }
            }
        }
    }

    fun saldo(): BigDecimal = comConexao { conn ->
        conn.prepareStatement("SELECT saldo FROM caixa WHERE id=1").use { stmt ->
            stmt.executeQuery().use { rs ->
                if (!rs.next()) BigDecimal.ZERO else rs.getBigDecimal("saldo")
            }
        }
    }

    override fun editar(item: Movimentacao, id: Int) {
        throw UnsupportedOperationException("Movimentações financeiras não podem ser editadas.")
    }

    override fun excluir(id: Int) {
        throw UnsupportedOperationException("Movimentações financeiras não podem ser excluídas.")
    }
}
