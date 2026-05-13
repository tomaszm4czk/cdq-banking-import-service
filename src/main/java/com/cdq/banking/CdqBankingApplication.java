package com.cdq.banking;

import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableMongock
@SpringBootApplication
public class CdqBankingApplication {

  public static void main(String[] args) {
    SpringApplication.run(CdqBankingApplication.class, args);
  }
}
