package com.cdq.banking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("CDQ Banking API")
                .description(
                    "REST API for importing bank account transactions from CSV files and presenting aggregated statistics per category, IBAN, and month.")
                .version("1.0.0")
                .contact(new Contact().name("CDQ Banking").email("contact@cdq.com")));
  }
}
