package repositorio

import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDateTime

class CRUDVenda : ConexaoPostgres() {

    fun venderProduto(
        clienteId: Int,
        produtoId: Int,
        quantidade: Int,
        responsavelId: Int,
        formaPagamento: String
    ) {
        require(quantidade > 0) { "Quantidade deve ser maior que zero." }
        require(formaPagamento.isNotBlank()) { "Forma de pagamento é obrigatória." }

        comConexao { conn ->
            conn.autoCommit = false
            try {
                val dados = conn.prepareStatement(
                    "SELECT preco, estoque, marca, modelo FROM caixa_da_agua WHERE id=? FOR UPDATE"
                ).use { stmt ->
                    stmt.setInt(1, produtoId)
                    stmt.executeQuery().use { rs ->
                        if (!rs.next()) throw IllegalArgumentException("Produto não encontrado.")
                        Triple(
                            rs.getBigDecimal("preco"),
                            rs.getInt("estoque"),
                            "${rs.getString("marca")} ${rs.getString("modelo")}"
                        )
                    }
                }

                if (dados.second < quantidade) {
                    throw IllegalArgumentException("Estoque insuficiente. Disponível: ${dados.second}.")
                }

                val cliente = conn.prepareStatement(
                    "SELECT p.nome FROM pessoa p JOIN cliente c ON c.pessoa_id=p.id WHERE p.id=?"
                ).use { stmt ->
                    stmt.setInt(1, clienteId)
                    stmt.executeQuery().use { rs ->
                        if (!rs.next()) throw IllegalArgumentException("Cliente não encontrado.")
                        rs.getString(1)
                    }
                }

                conn.prepareStatement(
                    "SELECT p.nome FROM pessoa p JOIN instalador i ON i.pessoa_id=p.id WHERE p.id=?"
                ).use { stmt ->
                    stmt.setInt(1, responsavelId)
                    stmt.executeQuery().use { rs ->
                        if (!rs.next()) throw IllegalArgumentException("Responsável não encontrado.")
                    }
                }

                val total = dados.first.multiply(BigDecimal(quantidade))
                val agora = Timestamp.valueOf(LocalDateTime.now())

                val vendaId = conn.prepareStatement(
                    """
                    INSERT INTO venda(cliente_id, produto_id, quantidade, valor_total, data_venda,
                                      responsavel_id, forma_pagamento)
                    VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id
                    """.trimIndent()
                ).use { stmt ->
                    stmt.setInt(1, clienteId)
                    stmt.setInt(2, produtoId)
                    stmt.setInt(3, quantidade)
                    stmt.setBigDecimal(4, total)
                    stmt.setTimestamp(5, agora)
                    stmt.setInt(6, responsavelId)
                    stmt.setString(7, formaPagamento)
                    stmt.executeQuery().use { rs -> rs.next(); rs.getInt(1) }
                }

                conn.prepareStatement("UPDATE caixa_da_agua SET estoque=estoque-? WHERE id=?").use { stmt ->
                    stmt.setInt(1, quantidade)
                    stmt.setInt(2, produtoId)
                    stmt.executeUpdate()
                }

                conn.prepareStatement(
                    """
                    INSERT INTO movimentacao
                    (valor, data_hora, tipo, pagador, recebedor, responsavel_id, descricao, venda_id)
                    VALUES (?, ?, 'RECEITA', ?, 'CAIXA DA EMPRESA', ?, ?, ?)
                    """.trimIndent()
                ).use { stmt ->
                    stmt.setBigDecimal(1, total)
                    stmt.setTimestamp(2, agora)
                    stmt.setString(3, cliente)
                    stmt.setInt(4, responsavelId)
                    stmt.setString(5, "Venda #$vendaId - ${dados.third} - $quantidade unidade(s) - $formaPagamento")
                    stmt.setInt(6, vendaId)
                    stmt.executeUpdate()
                }

                conn.prepareStatement("UPDATE caixa SET saldo=saldo+? WHERE id=1").use { stmt ->
                    stmt.setBigDecimal(1, total)
                    stmt.executeUpdate()
                }

                conn.commit()
                println("Venda #$vendaId realizada. Total: R$ $total")
            } catch (e: Exception) {
                conn.rollback()
                throw e
            } finally {
                conn.autoCommit = true
            }
        }
    }

    fun comprarEstoque(
        produtoId: Int,
        quantidade: Int,
        custoUnitario: BigDecimal,
        fornecedor: String,
        responsavelId: Int
    ) {
        require(quantidade > 0) { "Quantidade deve ser maior que zero." }
        require(custoUnitario > BigDecimal.ZERO) { "Custo deve ser maior que zero." }
        require(fornecedor.isNotBlank()) { "Fornecedor é obrigatório." }

        comConexao { conn ->
            conn.autoCommit = false
            try {
                val produto = conn.prepareStatement(
                    "SELECT marca, modelo FROM caixa_da_agua WHERE id=? FOR UPDATE"
                ).use { stmt ->
                    stmt.setInt(1, produtoId)
                    stmt.executeQuery().use { rs ->
                        if (!rs.next()) throw IllegalArgumentException("Produto não encontrado.")
                        "${rs.getString("marca")} ${rs.getString("modelo")}"
                    }
                }

                conn.prepareStatement(
                    "SELECT 1 FROM instalador WHERE pessoa_id=?"
                ).use { stmt ->
                    stmt.setInt(1, responsavelId)
                    stmt.executeQuery().use { rs ->
                        if (!rs.next()) throw IllegalArgumentException("Responsável não encontrado.")
                    }
                }

                val total = custoUnitario.multiply(BigDecimal(quantidade))
                val agora = Timestamp.valueOf(LocalDateTime.now())

                conn.prepareStatement("UPDATE caixa_da_agua SET estoque=estoque+? WHERE id=?").use { stmt ->
                    stmt.setInt(1, quantidade)
                    stmt.setInt(2, produtoId)
                    stmt.executeUpdate()
                }

                conn.prepareStatement(
                    """
                    INSERT INTO movimentacao
                    (valor, data_hora, tipo, pagador, recebedor, responsavel_id, descricao)
                    VALUES (?, ?, 'DESPESA', 'CAIXA DA EMPRESA', ?, ?, ?)
                    """.trimIndent()
                ).use { stmt ->
                    stmt.setBigDecimal(1, total)
                    stmt.setTimestamp(2, agora)
                    stmt.setString(3, fornecedor)
                    stmt.setInt(4, responsavelId)
                    stmt.setString(5, "Compra de estoque - $produto - $quantidade unidade(s)")
                    stmt.executeUpdate()
                }

                conn.prepareStatement("UPDATE caixa SET saldo=saldo-? WHERE id=1 AND saldo>=?").use { stmt ->
                    stmt.setBigDecimal(1, total)
                    stmt.setBigDecimal(2, total)
                    if (stmt.executeUpdate() == 0) throw IllegalStateException("Saldo insuficiente para a compra.")
                }

                conn.commit()
                println("Compra registrada. Total pago: R$ $total")
            } catch (e: Exception) {
                conn.rollback()
                throw e
            } finally {
                conn.autoCommit = true
            }
        }
    }

    fun listar() {
        comConexao { conn ->
            conn.prepareStatement(
                """
                SELECT v.id, p.nome AS cliente, c.marca || ' ' || c.modelo AS produto,
                       v.quantidade, v.valor_total, v.data_venda, v.forma_pagamento,
                       r.nome AS responsavel
                FROM venda v
                JOIN pessoa p ON p.id=v.cliente_id
                JOIN caixa_da_agua c ON c.id=v.produto_id
                JOIN instalador i ON i.pessoa_id=v.responsavel_id
                JOIN pessoa r ON r.id=i.pessoa_id
                ORDER BY v.data_venda DESC
                """.trimIndent()
            ).use { stmt ->
                stmt.executeQuery().use { rs ->
                    println("\n--- VENDAS ---")
                    var encontrou = false
                    while (rs.next()) {
                        encontrou = true
                        println(
                            "Venda #${rs.getInt("id")} | cliente=${rs.getString("cliente")} | " +
                            "produto=${rs.getString("produto")} | qtd=${rs.getInt("quantidade")} | " +
                            "total=R$ ${rs.getBigDecimal("valor_total")} | ${rs.getTimestamp("data_venda")} | " +
                            "${rs.getString("forma_pagamento")} | responsável=${rs.getString("responsavel")}"
                        )
                    }
                    if (!encontrou) println("Nenhuma venda registrada.")
                }
            }
        }
    }
}


/*11.(GENERALIZADAS) MOVIMENTAÇÃO FINANCEIRA
*
*EM venderProduto() A MOVIMENTAÇÃO É CRIADA DEPOIS QUE A VENDA É REGISTRADA
*E O ESTOQUE É ATUALIZADO
* */


/*12. (GENERALIZADA) COMPRA/VENDA GERA MOVIMENTAÇÃO
*
* A VENDA É FEITA DENTRO DE UMA UNICA TRANSAÇÃO. O SISTEMA CRIA A VENDA, DIMINUI O ESTOQUE
* CRIA A MOVIMENTALÇAO FINANCEIRA ATUALIZA O CAIXA E SÓ DEPOIS EXECUTA O COMMIT.
* */

/*13. (GENERALIZADAS)
*
* O FOR UPDATE BLOQUEIA O REGISTRO DO PRODUTO DURANTE A TRANSAÇÃO, ASSIM SE DUAS
* PESSOAS TENTAREM VENDER O MSMO PRODUTO SIMULTANEAMENTE O BANCO CONTROLA O ACESSO
* E EVITA QUE AS DUAS OPERAÇÕEES CONSUMAM O MESMO ESTOQUE DE FORMA INCORRETA
*/



/*18 (GENERALIZADAS) BANCO CAI DURANTE UMA VENDA
*
*SE O BANCO CAIR ANTES DO COMMIT, A TRANSAÇÃO É REVERTIDA COM ROLLBACK, ASSIM EVITAMOS
* DEIXAR A VENDA PARCIALMENTE REGISTRADA
* */