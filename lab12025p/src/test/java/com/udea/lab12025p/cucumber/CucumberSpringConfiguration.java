package com.udea.lab12025p.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

// Le dice a Cucumber que use el contexto de Spring Boot
@CucumberContextConfiguration
@SpringBootTest
public class CucumberSpringConfiguration {
}