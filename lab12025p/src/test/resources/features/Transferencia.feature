Feature: Transferencia de Dinero
  Como cliente del Banco FullStack
  Quiero poder transferir dinero de mi cuenta a otra cuenta
  Para poder realizar pagos y enviar dinero a terceros a través de Internet

  # CASO DE NEGOCIO PRIMARIO (Happy Path)
  Scenario: Transferencia Exitosa de fondos entre dos cuentas validas
    Given que el cliente "Homero" tiene una cuenta "0001" con saldo de 1000.00
    And que el cliente "Ned" tiene una cuenta "0002" con saldo de 500.00
    When "Homero" transfiere 200.00 a la cuenta de "Ned"
    Then la transferencia es aprobada
    And el nuevo saldo de "Homero" debe ser 800.00
    And el nuevo saldo de "Ned" debe ser 700.00

  # CASOS ALTERNOS (Reglas de Control de Riesgo)
  Scenario: Transferencia Rechazada por falta de fondos
    Given que el cliente "Homero" tiene una cuenta "0001" con saldo de 100.00
    And que el cliente "Ned" tiene una cuenta "0002" con saldo de 500.00
    When "Homero" intenta transferir 500.00 a la cuenta de "Ned"
    Then la transferencia es denegada con el mensaje "El remitente no tiene saldo suficiente"
    And el saldo de "Homero" permanece en 100.00
    And el saldo de "Ned" permanece en 500.00

  Scenario: Transferencia Rechazada por cuenta destino invalida
    Given que el cliente "Homero" tiene una cuenta "0001" con saldo de 1000.00
    When "Homero" intenta transferir 100.00 a la cuenta inexistente "9999"
    Then la transferencia es denegada con el mensaje "La cuenta del receptor no existe"
