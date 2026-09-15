package produto

import enumeradores.Cor
import enumeradores.Material
import java.math.BigDecimal


/*1.(GENERALIZADAS)
* OS ATRIBUTOS REPRESENTAM AS INFORMAÇÕES QUE O SISTEMA PRECISA PRA IDENTIFICAR,
* VENDER E CONTROLAR O PRODUTO
* */
class CaixaDaAgua(
    val id: Int? = null,
    val marca: String,
    val modelo: String,
    val dimensao: MutableList<Double>,
    val cor: Cor,
    val material: Material,
    val formato: String,
    val preco: BigDecimal,
    val estoque: Int = 0
)
