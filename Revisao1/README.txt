SISTEMA CAIXA D'ÁGUA - KOTLIN + POSTGRESQL

1. BANCO DE DADOS
-----------------
Crie o banco PostgreSQL chamado "caixaDaAgua" e, conectado nele, execute o arquivo banco.sql.

O programa espera, por padrão:
- Banco: caixaDaAgua
- Usuário: postgres
- Senha: postgres
- Porta: 5432

Se sua configuração for diferente, use as variáveis de ambiente:
POSTGRES_URL
POSTGRES_USER
POSTGRES_PASSWORD

Exemplo:
POSTGRES_URL=jdbc:postgresql://localhost:5432/caixaDaAgua
POSTGRES_USER=postgres
POSTGRES_PASSWORD=sua_senha

2. DRIVER JDBC
--------------
Adicione o driver JDBC oficial do PostgreSQL às bibliotecas do IntelliJ.
O projeto original já usava uma biblioteca chamada "driverpostgres".
Se você mantiver essa biblioteca, não precisa alterar o código.

3. ESTRUTURA
------------
A estrutura de src foi mantida conforme o projeto enviado:
src/
  enumeradores/
  financeiro/
  pessoas/
  produto/
  repositorio/
  sistema/
    caixa_da_agua/
    clientes/
    financeiro/
    funcionarios/
    pagamentos/
    vendas/

4. FUNCIONALIDADES
------------------
- Menu interativo via console.
- CRUD de caixas d'água.
- Controle de estoque.
- Cadastro, listagem, edição e exclusão de clientes.
- Cadastro, listagem, edição e exclusão de funcionários.
- Funcionários separados em setores: Administrativo, Financeiro e Logística.
- Registro de compra/entrada de estoque.
- Registro de venda.
- Fluxo de caixa.
- Registro de receitas e despesas.
- Registro de valor, pagador, recebedor, data/hora, descrição e responsável.
- Transações financeiras com commit/rollback.
- Validação com Regex, toIntOrNull, BigDecimal e nullable.
- Movimentações financeiras não podem ser editadas/excluídas pelo CRUD.

5. OBSERVAÇÃO
-------------
No pgAdmin, crie o banco caixaDaAgua, conecte-se a ele e execute o banco.sql.

6. ORDEM RECOMENDADA PARA TESTAR
--------------------------------
1) Cadastre um funcionário.
2) Cadastre um cliente.
3) Cadastre uma caixa d'água com estoque inicial.
4) Faça uma venda.
5) Entre em Financeiro e confira o saldo e a movimentação.
6) Faça uma entrada de estoque/compra.
7) Confira novamente o saldo e o estoque.
