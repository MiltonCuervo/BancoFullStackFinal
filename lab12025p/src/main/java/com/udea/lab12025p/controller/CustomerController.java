package com.udea.lab12025p.controller;

import com.udea.lab12025p.DTO.CustomerDTO;
import com.udea.lab12025p.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "http://localhost:3000")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    //Obtener todos los clientes
    //Recursos HTTP --> URL
    //Medtodos HTTP --> GET, POST, PUT, DELETE
    //Representacion del Recurso JSON / XML / Texto plano

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    //Obtener un cliente por su ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO) {
        if(customerDTO.getBalance() == null){
            throw new IllegalArgumentException("El saldo no puede ser nulo");
        }
        return ResponseEntity.ok(customerService.createCustomer(customerDTO));
    }

    /**
     * Endpoint para actualizar un cliente existente por su ID.
     * Se invoca con una petición PUT a /api/customers/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) {
        try {
            CustomerDTO updatedCustomer = customerService.updateCustomer(id, customerDTO);
            return ResponseEntity.ok(updatedCustomer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para borrar un cliente por su ID.
     * Se invoca con una petición DELETE a /api/customers/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        try {
            customerService.deleteCustomer(id);
            // Retorna un código 204 No Content, que es el estándar para borrados exitosos.
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
