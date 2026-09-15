package pessoas

import enumeradores.Habilidade
import enumeradores.Turno
import financeiro.Movimentacao
import java.math.BigDecimal
import java.time.LocalDateTime

class Instalador(
    id: Int? = null,
    nome: String,
    cpf: String,
    idade: Int,
    val salario: BigDecimal = BigDecimal("2000.00"),
    val turno: Turno,
    val habilidade: Habilidade,
    val setorId: Int? = null
) : Pessoa(id, nome, cpf, idade) {

    override fun receberConta(valor: BigDecimal, conta: Pessoa): Movimentacao {
        return Movimentacao(
            valor = valor,
            dataMovimentacao = LocalDateTime.now(),
            contexto = "Pagamento/recebimento relacionado a funcionário"
        )
    }
}

/*3.(GENERALIZADAS):
*EXISTE O MÉTODO OVERRIDE, ENTÃO O MECANISMO DE POLIMORFISMO ESTA PRESENTE. PORÉM NO FLUXO
* PRINCIPAL DO SISTEMA ELE NÃO É MUITO EXPLORADO ATRAV´S DE UMA REFERENCIA DA SUPERCLASSE
*/

/*25 (GENERALIZADAS) POR QUE USAR NULLABLE?
*
* ASSIM COMO EM PESSOA, CLIENTE EE NA MOVIMENTACAO, USEI O NULL PORQUE O ID DO INSTALADOR
* NA LINHA 10 POR EXMPLO, PODE SER NULO ANTES DO OBJETO SER SALVO NO BANCO, PORQUE O
* POSTGRES GERA O ID AUTOMATICAMENTE
* */