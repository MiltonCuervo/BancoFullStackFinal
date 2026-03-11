package com.udea.lab12025p.cucumber;

import com.udea.lab12025p.DTO.TransactionDTO;
import com.udea.lab12025p.entity.Customer;
import com.udea.lab12025p.repository.CustomerRepository;
import com.udea.lab12025p.service.TransactionService;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@ActiveProfiles("test")
public class TransferenciaSteps {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CustomerRepository customerRepository;

    private Map<String, Customer> clientes = new HashMap<>();
    private TransactionDTO transaccionRequest;
    private TransactionDTO transaccionResultado;
    private Exception ex;

    @Before
    public void limpiarBase() {
        customerRepository.deleteAll();
    }

    @Given("que el cliente {string} tiene una cuenta {string} con saldo de {double}")
    public void registrarEstadoInicial(String nombre, String cuenta, double saldoInicial) {

        Customer cliente = new Customer();
        cliente.setAccountNumber(cuenta);
        cliente.setFirstName(nombre);
        cliente.setLastName("Apellido");
        cliente.setBalance(BigDecimal.valueOf(saldoInicial));

        customerRepository.save(cliente);
        clientes.put(nombre, cliente);
    }

    @When("{string} transfiere {double} a la cuenta de {string}")
    public void ejecutaTransferenciaExitosa(String cliente1, double monto, String cliente2) {
        try {
            Customer remitente = clientes.get(cliente1);
            Customer receptor = clientes.get(cliente2);

            transaccionRequest = new TransactionDTO();
            transaccionRequest.setSenderAccountNumber(remitente.getAccountNumber());
            transaccionRequest.setReceiverAccountNumber(receptor.getAccountNumber());
            transaccionRequest.setAmount(BigDecimal.valueOf(monto));

            transaccionResultado = transactionService.transferMoney(transaccionRequest);
        } catch (Exception e) {
            ex = e;
        }
    }

    @When("{string} intenta transferir {double} a la cuenta inexistente {string}")
    public void ejectuaTransferenciaFallidaMalaCuenta(String nombre, double monto, String malaCuenta) {
        try {
            Customer remitente = clientes.get(nombre);

            transaccionRequest = new TransactionDTO();
            transaccionRequest.setSenderAccountNumber(remitente.getAccountNumber());
            transaccionRequest.setReceiverAccountNumber(malaCuenta);
            transaccionRequest.setAmount(BigDecimal.valueOf(monto));

            transactionService.transferMoney(transaccionRequest);
        } catch (Exception e) {
            ex = e;
        }
    }

    @When("{string} intenta transferir {double} a la cuenta de {string}")
    public void ejectuaTransferenciaFallidaMonto(String cliente1, double monto, String cliente2) {
        try {
            Customer remitente = clientes.get(cliente1);
            Customer receptor = clientes.get(cliente2);

            transaccionRequest = new TransactionDTO();
            transaccionRequest.setSenderAccountNumber(remitente.getAccountNumber());
            transaccionRequest.setReceiverAccountNumber(receptor.getAccountNumber());
            transaccionRequest.setAmount(BigDecimal.valueOf(monto));

            transactionService.transferMoney(transaccionRequest);
        } catch (Exception e) {
            ex = e;
        }
    }

    @Then("la transferencia es aprobada")
    public void assertTransferenciaAprobada() {
        assertNotNull(transaccionResultado);
        assertNotNull(transaccionResultado.getId());
    }

    @Then("la transferencia es denegada con el mensaje {string}")
    public void assertMensajeDeError(String mensajeEsperado) {
        assertNotNull(ex);
        assertEquals(mensajeEsperado, ex.getMessage());
    }

    @And("el nuevo saldo de {string} debe ser {double}")
        public void chequearNuevosSaldos(String nombre, double saldoEsperado) {
            Customer cliente = customerRepository.findByFirstName(nombre);
            
            assertNotNull(cliente, "No se encontró al cliente: " + nombre);
        
            // Usamos compareTo porque con BigDecimal el .equals es muy cansón con los decimales
            BigDecimal esperado = BigDecimal.valueOf(saldoEsperado);
            assertEquals(0, esperado.compareTo(cliente.getBalance()), 
                "El saldo de " + nombre + " no es el esperado. Tiene: " + cliente.getBalance());
        }

    @And("el saldo de {string} permanece en {double}")
        public void chequearSaldosIntactos(String nombre, double saldoEsperado) {
            Customer cliente = customerRepository.findByFirstName(nombre);
            
            assertNotNull(cliente, "No se encontró al cliente: " + nombre);

            BigDecimal esperado = BigDecimal.valueOf(saldoEsperado);
            assertEquals(0, esperado.compareTo(cliente.getBalance()), 
                "El saldo de " + nombre + " cambió y debía quedarse quieto.");
        }
}
