package repositorio

import produto.CaixaDaAgua

class CRUDCaixaDaAgua : interfaceJPA<CaixaDaAgua>, ConexaoPostgres() {

    override fun salvar(item: CaixaDaAgua) {
        require(item.marca.isNotBlank()) { "A marca é obrigatória." }
        require(item.modelo.isNotBlank()) { "O modelo é obrigatório." }
        require(item.dimensao.size == 3 && item.dimensao.all { it > 0 }) { "Dimensões inválidas." }
        require(item.preco > java.math.BigDecimal.ZERO) { "Preço deve ser maior que zero." }
        require(item.estoque >= 0) { "Estoque não pode ser negativo." }

        comConexao { conn ->
            val sql = """
                INSERT INTO caixa_da_agua
                (marca, modelo, dimensao, cor, material, formato, preco, estoque)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent()
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, item.marca)
                stmt.setString(2, item.modelo)
                stmt.setArray(3, conn.createArrayOf("float8", item.dimensao.toTypedArray()))
                stmt.setString(4, item.cor.name)
                stmt.setString(5, item.material.name)
                stmt.setString(6, item.formato)
                stmt.setBigDecimal(7, item.preco)
                stmt.setInt(8, item.estoque)
                stmt.executeUpdate()
            }
        }
        println("Caixa d'água cadastrada com sucesso.")
    }

    override fun listar() {
        comConexao { conn ->
            conn.prepareStatement(
                "SELECT id, marca, modelo, dimensao, cor, material, formato, preco, estoque FROM caixa_da_agua ORDER BY id"
            ).use { stmt ->
                stmt.executeQuery().use { rs ->
                    println("\n--- CAIXAS D'ÁGUA ---")
                    var encontrou = false
                    while (rs.next()) {
                        encontrou = true
                        val dimensoes = (rs.getArray("dimensao").array as Array<*>).contentToString()
                        println(
                            "ID ${rs.getInt("id")} | ${rs.getString("marca")} ${rs.getString("modelo")} | " +
                            "dim=$dimensoes | cor=${rs.getString("cor")} | material=${rs.getString("material")} | " +
                            "formato=${rs.getString("formato")} | preço=R$ ${rs.getBigDecimal("preco")} | " +
                            "estoque=${rs.getInt("estoque")}"
                        )
                    }
                    if (!encontrou) println("Nenhum produto cadastrado.")
                }
            }
        }
    }

    override fun editar(item: CaixaDaAgua, id: Int) {
        require(id > 0) { "ID inválido." }
        comConexao { conn ->
            val sql = """
                UPDATE caixa_da_agua
                SET marca=?, modelo=?, dimensao=?, cor=?, material=?, formato=?, preco=?
                WHERE id=?
            """.trimIndent()
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, item.marca)
                stmt.setString(2, item.modelo)
                stmt.setArray(3, conn.createArrayOf("float8", item.dimensao.toTypedArray()))
                stmt.setString(4, item.cor.name)
                stmt.setString(5, item.material.name)
                stmt.setString(6, item.formato)
                stmt.setBigDecimal(7, item.preco)
                stmt.setInt(8, id)
                if (stmt.executeUpdate() == 0) throw IllegalArgumentException("Produto não encontrado.")
            }
        }
        println("Produto atualizado com sucesso.")
    }

    fun alterarEstoque(id: Int, quantidade: Int, connExterno: java.sql.Connection? = null) {
        require(id > 0) { "ID inválido." }
        require(quantidade != 0) { "A quantidade não pode ser zero." }
        if (connExterno != null) alterarEstoqueNaConexao(connExterno, id, quantidade)
        else comConexao { conn -> alterarEstoqueNaConexao(conn, id, quantidade) }
    }

    private fun alterarEstoqueNaConexao(conn: java.sql.Connection, id: Int, quantidade: Int) {
        val sql = """
            UPDATE caixa_da_agua
            SET estoque = estoque + ?
            WHERE id = ? AND estoque + ? >= 0
        """.trimIndent()
        conn.prepareStatement(sql).use { stmt ->
            stmt.setInt(1, quantidade)
            stmt.setInt(2, id)
            stmt.setInt(3, quantidade)
            if (stmt.executeUpdate() == 0) {
                throw IllegalArgumentException("Produto inexistente ou estoque insuficiente.")
            }
        }
    }

    override fun excluir(id: Int) {
        comConexao { conn ->
            conn.prepareStatement("DELETE FROM caixa_da_agua WHERE id=?").use { stmt ->
                stmt.setInt(1, id)
                if (stmt.executeUpdate() == 0) throw IllegalArgumentException("Produto não encontrado.")
            }
        }
        println("Produto excluído com sucesso.")
    }
}


/*17 (GENERALIZADAS) COMO UMA CONSULTA FUNCIONA
*
* PRIMEIRO O USUÁRIO ESCOLHE UMA OPÇÃO NO MENU, O MENU CHAMA O CRUD, O CRUD ABRE A CONEXÃO
*PREPARA O SQL, EXECUTA A CONSULTA, RECEBE O ResultSet E MOSTRA OS DADOS
* */