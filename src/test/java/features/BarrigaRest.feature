#Author: your.email@your.domain.com

@barrigaTest
Feature: Barriga Rest

  @barrigaTest1
  Scenario: Não deve acessar a aplicação sem um token de autenticação
    Given que tento acessar a aplicação sem um token de acesso
    Then vejo que não é possível acessar a aplicação

  @barrigaTest2
  Scenario: Deve incluir uma conta com sucesso
    Given que realizo meu logon na aplicação
    And realizo a inclusão de uma nova conta
    Then confirmo que a conta foi gerada com sucesso

  @barrigaTest3
  Scenario: Não deve incluir conta com nome repetido
    Given que realizo meu logon na aplicação
    And tento incluir uma conta com um nome já registrado
    Then confirmo não ser possível incluir contas com nomes iguais

  @barrigaTest4
  Scenario: Deve alterar o nome de uma conta com sucesso
    Given que realizo meu logon na aplicação
    And seleciono uma conta cadastrada
    When alterar o nome desta conta
    Then confirmo que nome foi alterado com sucesso

  @barrigaTest5
  Scenario: Deve validar os campos obrigatórios na inclusão de uma movimentação
    Given que realizo meu logon na aplicação
    And seleciono uma conta cadastrada
    When tentar incluir uma movimentação nesta conta sem informar os dados obrigatórios
    Then confirmo que não é possível incluir a movimentação sem esses dados

  @barrigaTest6
  Scenario: Não deve cadastrar movimentação com data futura para pagamento
    Given que realizo meu logon na aplicação
    And seleciono uma conta cadastrada
    When tentar incluir uma movimentação com uma data futura para pagamento
    Then confirmo que não é possível incluir a movimentação com esta data

  @barrigaTest7
  Scenario: Deve listar o saldo das movimentações de uma conta
    Given que realizo meu logon na aplicação
    And seleciono uma conta cadastrada com movimentações
    When consultar a lista de movimentações desta conta
    Then confirmo que todos os dados de saldos serão informados

  @barrigaTest8
  Scenario: Não deve remover uma conta que tenha uma movimentação vinculada
    Given que realizo meu logon na aplicação
    And seleciono uma conta cadastrada com movimentações
    When tentar excluir essa conta
    Then confirmo que não é possível excluir uma conta com movimentações

  @barrigaTest9
  Scenario: Deve remover uma movimentação
    Given que realizo meu logon na aplicação
    And seleciono uma conta cadastrada com movimentações
    When tentar excluir uma movimentação
    Then confirmo que a movimentação é excluída com sucesso
    

 
