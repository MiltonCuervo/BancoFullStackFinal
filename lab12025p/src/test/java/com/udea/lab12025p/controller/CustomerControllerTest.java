package com.udea.lab12025p.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udea.lab12025p.dto.CustomerDTO;
import com.udea.lab12025p.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private CustomerDTO sampleCustomer;

    @BeforeEach
    void setUp() {
        // MockMvc standalone: sin ApplicationContext, puro Mockito
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
        objectMapper = new ObjectMapper();

        sampleCustomer = new CustomerDTO();
        sampleCustomer.setFirstName("Ana");
        sampleCustomer.setLastName("López");
        sampleCustomer.setAccountNumber("9999");
        sampleCustomer.setBalance(new BigDecimal("800.00"));
    }

    // TEST 1: GET /api/customers retorna lista
    @Test
    void testGetAllCustomers_ReturnsOk() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(List.of(sampleCustomer));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Ana"));
    }

    // TEST 2: GET /api/customers/{id} retorna cliente
    @Test
    void testGetCustomerById_ReturnsOk() throws Exception {
        when(customerService.getCustomerById(1L)).thenReturn(sampleCustomer);

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("9999"));
    }

    // TEST 3: POST /api/customers crea cliente con saldo válido
    @Test
    void testCreateCustomer_WithBalance_ReturnsOk() throws Exception {
        when(customerService.createCustomer(any(CustomerDTO.class))).thenReturn(sampleCustomer);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ana"));
    }

    // TEST 4: POST sin saldo lanza IllegalArgumentException directamente desde el controlador
    @Test
    void testCreateCustomer_WithNullBalance_ThrowsIllegalArgumentException() {
        sampleCustomer.setBalance(null);

        // Verificamos directamente que el controlador lanza la excepción
        IllegalArgumentException ex = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> customerController.createCustomer(sampleCustomer)
        );

        assertEquals("El saldo no puede ser nulo", ex.getMessage());
        // El servicio nunca debe llamarse si el balance es nulo
        verify(customerService, never()).createCustomer(any(CustomerDTO.class));
    }

    // TEST 5: PUT actualiza cliente existente
    @Test
    void testUpdateCustomer_Success_ReturnsOk() throws Exception {
        when(customerService.updateCustomer(eq(1L), any(CustomerDTO.class))).thenReturn(sampleCustomer);

        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ana"));
    }

    // TEST 6: PUT con cliente inexistente retorna 404
    @Test
    void testUpdateCustomer_NotFound_Returns404() throws Exception {
        when(customerService.updateCustomer(eq(99L), any(CustomerDTO.class)))
                .thenThrow(new IllegalArgumentException("Cliente no encontrado con ID: 99"));

        mockMvc.perform(put("/api/customers/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleCustomer)))
                .andExpect(status().isNotFound());
    }

    // TEST 7: DELETE borra cliente exitosamente
    @Test
    void testDeleteCustomer_Success_ReturnsNoContent() throws Exception {
        doNothing().when(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNoContent());
    }

    // TEST 8: DELETE con cliente inexistente retorna 404
    @Test
    void testDeleteCustomer_NotFound_Returns404() throws Exception {
        doThrow(new IllegalArgumentException("Cliente no encontrado con ID: 99"))
                .when(customerService).deleteCustomer(99L);

        mockMvc.perform(delete("/api/customers/99"))
                .andExpect(status().isNotFound());
    }
}
