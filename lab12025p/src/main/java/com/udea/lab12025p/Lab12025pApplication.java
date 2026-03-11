package com.udea.lab12025p;

import com.udea.lab12025p.entity.Customer;
import com.udea.lab12025p.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
public class Lab12025pApplication {

	public static void main(String[] args) {
		SpringApplication.run(Lab12025pApplication.class, args);
	}

	@Bean
	public CommandLineRunner setupData(CustomerRepository customerRepository) {
		return args -> {
			// Crear cliente 1 si no existe
			if (customerRepository.findByAccountNumber("0001").isEmpty()) {
				Customer c1 = new Customer();
				c1.setFirstName("Homero");
				c1.setLastName("Simpson");
				c1.setAccountNumber("0001");
				c1.setBalance(new BigDecimal("1000.00"));
				customerRepository.save(c1);
			}

			// Crear cliente 2 si no existe
			if (customerRepository.findByAccountNumber("0002").isEmpty()) {
				Customer c2 = new Customer();
				c2.setFirstName("Ned");
				c2.setLastName("Flanders");
				c2.setAccountNumber("0002");
				c2.setBalance(new BigDecimal("500.00"));
				customerRepository.save(c2);
			}
		};
	}
}
