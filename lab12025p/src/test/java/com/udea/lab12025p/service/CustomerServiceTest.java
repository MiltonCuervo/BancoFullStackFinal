package com.udea.lab12025p.service;

import com.udea.lab12025p.dto.CustomerDTO;
import com.udea.lab12025p.entity.Customer;
import com.udea.lab12025p.mapper.CustomerMapper;
import com.udea.lab12025p.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    private Customer customerEntity;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customerEntity = new Customer();
        customerEntity.setId(1L);
        customerEntity.setFirstName("Juan");
        customerEntity.setLastName("Pérez");
        customerEntity.setAccountNumber("1111");
        customerEntity.setBalance(new BigDecimal("500.00"));

        customerDTO = new CustomerDTO();
        customerDTO.setFirstName("Juan");
        customerDTO.setLastName("Pérez");
        customerDTO.setAccountNumber("1111");
        customerDTO.setBalance(new BigDecimal("500.00"));
    }

    // TEST 1: Obtener todos los clientes
    @Test
    void testGetAllCustomers_ReturnsList() {
        when(customerRepository.findAll()).thenReturn(List.of(customerEntity));
        when(customerMapper.toDTO(customerEntity)).thenReturn(customerDTO);

        List<CustomerDTO> result = customerService.getAllCustomers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getFirstName());
        verify(customerRepository, times(1)).findAll();
    }

    // TEST 2: Obtener cliente por ID existente
    @Test
    void testGetCustomerById_Found() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customerEntity));
        when(customerMapper.toDTO(customerEntity)).thenReturn(customerDTO);

        CustomerDTO result = customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals("Juan", result.getFirstName());
        verify(customerRepository, times(1)).findById(1L);
    }

    // TEST 3: Obtener cliente por ID no existente lanza excepción
    @Test
    void testGetCustomerById_NotFound_ThrowsException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                customerService.getCustomerById(99L));

        assertEquals("Cliente no encontrado", ex.getMessage());
    }

    // TEST 4: Crear nuevo cliente
    @Test
    void testCreateCustomer_Success() {
        when(customerMapper.toEntity(customerDTO)).thenReturn(customerEntity);
        when(customerRepository.save(customerEntity)).thenReturn(customerEntity);
        when(customerMapper.toDTO(customerEntity)).thenReturn(customerDTO);

        CustomerDTO result = customerService.createCustomer(customerDTO);

        assertNotNull(result);
        assertEquals("1111", result.getAccountNumber());
        verify(customerRepository, times(1)).save(customerEntity);
    }

    // TEST 5: Actualizar cliente existente
    @Test
    void testUpdateCustomer_Success() {
        CustomerDTO updateRequest = new CustomerDTO();
        updateRequest.setFirstName("Pedro");
        updateRequest.setLastName("García");

        Customer updatedEntity = new Customer();
        updatedEntity.setId(1L);
        updatedEntity.setFirstName("Pedro");
        updatedEntity.setLastName("García");

        CustomerDTO updatedDTO = new CustomerDTO();
        updatedDTO.setFirstName("Pedro");
        updatedDTO.setLastName("García");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customerEntity));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedEntity);
        when(customerMapper.toDTO(updatedEntity)).thenReturn(updatedDTO);

        CustomerDTO result = customerService.updateCustomer(1L, updateRequest);

        assertNotNull(result);
        assertEquals("Pedro", result.getFirstName());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    // TEST 6: Actualizar cliente que no existe lanza excepción
    @Test
    void testUpdateCustomer_NotFound_ThrowsException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                customerService.updateCustomer(99L, customerDTO));

        assertTrue(ex.getMessage().contains("99"));
    }

    // TEST 7: Borrar cliente existente
    @Test
    void testDeleteCustomer_Success() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(1L);

        assertDoesNotThrow(() -> customerService.deleteCustomer(1L));
        verify(customerRepository, times(1)).deleteById(1L);
    }

    // TEST 8: Borrar cliente que no existe lanza excepción
    @Test
    void testDeleteCustomer_NotFound_ThrowsException() {
        when(customerRepository.existsById(99L)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                customerService.deleteCustomer(99L));

        assertTrue(ex.getMessage().contains("99"));
        verify(customerRepository, never()).deleteById(anyLong());
    }
}
