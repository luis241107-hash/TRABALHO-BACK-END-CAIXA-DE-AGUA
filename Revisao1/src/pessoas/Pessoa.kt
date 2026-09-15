package pessoas

import financeiro.Movimentacao
import java.math.BigDecimal
import java.time.LocalDateTime

open class Pessoa(
    val id: Int? = null,
    val nome: String,
    val cpf: String,
    val idade: Int
) {
    open fun receberConta(valor: BigDecimal, conta: Pessoa): Movimentacao {
        return Movimentacao(
            valor = valor,
            dataMovimentacao = LocalDateTime.now(),
            contexto = "Recebimento de conta"
        )
    }
}

/*2(GENERALIZADAS) -> CLIENTE -> INSTALADOR -> PESSOA

USEI HERANÇA PORQUE O CLIENTE E O INSTALADOR SÃO TIPOS DE PESSOAS E COMPARTILHAM ATRIBUTOS E
INFORMAÇÕES.*/


/*3(GNERALIZADAS) POLIMORFISMO
* Sim. O método receberConta() existe em Pessoa e é sobrescrito em Instalador usando override.
* Assim, uma classe derivada pode ter um comportamento diferente para o mesmo método
* */

/*5.(GENERALIZADAS) CRIAR ESTÁGIARIO
* CRIARIA A CLASSE DENTRO DE PESSOAS E DEPOIS CRIARIA OU ADAPTARIA O CRUD, O MENU E O BD,
* CASO O ESTAGIARIO TIVESSE INFORÇÕES ESPECIFICAS
* */

