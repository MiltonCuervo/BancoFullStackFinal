package com.udea.lab12025p.service;

import com.udea.lab12025p.DTO.CustomerDTO;
import com.udea.lab12025p.entity.Customer;
import com.udea.lab12025p.mapper.CustomerMapper;
import com.udea.lab12025p.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    //Obtener la informacion de todos los clientes
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toDTO).toList();
    }

    //Obtener el dato de un cliente por un ID
    public CustomerDTO getCustomerById(Long id) {
        return customerRepository.findById(id).map(customerMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    //Crear un nuevo cliente
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        Customer customer = customerMapper.toEntity(customerDTO);
        return customerMapper.toDTO(customerRepository.save(customer));
    }

    //Actualiza la información de un cliente existente.

    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        // 1. Buscar el cliente en la base de datos por su ID.
        Customer customerToUpdate = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));

        // 2. Actualizar los campos del cliente con los datos del DTO.
        customerToUpdate.setFirstName(customerDTO.getFirstName());
        customerToUpdate.setLastName(customerDTO.getLastName());

        // 3. Guardar el cliente actualizado en la base de datos.
        Customer updatedCustomer = customerRepository.save(customerToUpdate);

        // 4. Convertir la entidad actualizada a DTO y devolverla.
        return customerMapper.toDTO(updatedCustomer);
    }

    //Borra un cliente de la base de datos.

    public void deleteCustomer(Long id) {
        // 1. Verificar si el cliente existe antes de intentar borrarlo.
        if (!customerRepository.existsById(id)) {
            throw new IllegalArgumentException("Cliente no encontrado con ID: " + id);
        }
        // 2. Borrar el cliente.
        customerRepository.deleteById(id);
    }
}
