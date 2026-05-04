package com.udea.lab12025p.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udea.lab12025p.dto.TransactionDTO;
import com.udea.lab12025p.entity.Customer;
import com.udea.lab12025p.repository.CustomerRepository;
import com.udea.lab12025p.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        customerRepository.deleteAll();

        Customer sender = new Customer();
        sender.setAccountNumber("0001");
        sender.setFirstName("Homero");
        sender.setLastName("Simpson");
        sender.setBalance(new BigDecimal("1000.00"));
        customerRepository.save(sender);

        Customer receiver = new Customer();
        receiver.setAccountNumber("0002");
        receiver.setFirstName("Ned");
        receiver.setLastName("Flanders");
        receiver.setBalance(new BigDecimal("500.00"));
        customerRepository.save(receiver);
    }

    @Test
    void testTransferMoney_Success_Integration() throws Exception {
        TransactionDTO requestDTO = new TransactionDTO();
        requestDTO.setSenderAccountNumber("0001");
        requestDTO.setReceiverAccountNumber("0002");
        requestDTO.setAmount(new BigDecimal("200.00"));

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.amount").value(200.0));

        Customer updatedSender = customerRepository.findByAccountNumber("0001").get();
        assertEquals(0, new BigDecimal("800.00").compareTo(updatedSender.getBalance()));

        Customer updatedReceiver = customerRepository.findByAccountNumber("0002").get();
        assertEquals(0, new BigDecimal("700.00").compareTo(updatedReceiver.getBalance()));
    }

    @Test
    void testTransferMoney_ConcurrentRequests_DoubleSpend() throws Exception {
        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        Runnable concurrentTransferRequest = () -> {
            try {
                TransactionDTO requestDTO = new TransactionDTO();
                requestDTO.setSenderAccountNumber("0001");
                requestDTO.setReceiverAccountNumber("0002");
                requestDTO.setAmount(new BigDecimal("1000.00"));

                mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)));
            } catch (Exception e) {
                // Una de las peticiones rebotará
            } finally {
                latch.countDown();
            }
        };

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(concurrentTransferRequest);
        }

        latch.await(5, TimeUnit.SECONDS);

        Customer homer = customerRepository.findByAccountNumber("0001").get();
        assertEquals(0, new BigDecimal("0.00").compareTo(homer.getBalance()));
    }
}
