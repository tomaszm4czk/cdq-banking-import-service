package com.cdq.banking.importing.config;

import com.cdq.banking.importing.adapter.csv.CsvTransactionParser;
import com.cdq.banking.importing.domain.validation.TransactionValidator;
import com.cdq.banking.importing.service.TransactionFileParserRegistry;
import java.util.Map;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class ImportModuleConfig {

  @Bean
  public TransactionValidator transactionValidator() {
    return new TransactionValidator();
  }

  @Bean
  public TransactionFileParserRegistry transactionFileParserRegistry(
      TransactionValidator validator, @Value("${import.csv.max-rows:10000}") int maxRows) {
    return new TransactionFileParserRegistry(
        Map.of("csv", new CsvTransactionParser(validator, maxRows)));
  }

  @Bean("importExecutor")
  public Executor importExecutor(
      @Value("${import.executor.core-pool-size:2}") int corePoolSize,
      @Value("${import.executor.max-pool-size:4}") int maxPoolSize,
      @Value("${import.executor.queue-capacity:10}") int queueCapacity) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(corePoolSize);
    executor.setMaxPoolSize(maxPoolSize);
    executor.setQueueCapacity(queueCapacity);
    executor.setThreadNamePrefix("import-");
    executor.initialize();
    return executor;
  }
}
