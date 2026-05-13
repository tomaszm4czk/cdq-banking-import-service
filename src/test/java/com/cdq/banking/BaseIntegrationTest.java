package com.cdq.banking;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.MongoDBContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public abstract class BaseIntegrationTest {

  @ServiceConnection static final MongoDBContainer mongoDBContainer;

  static {
    mongoDBContainer = new MongoDBContainer("mongo:8.0.10");
    mongoDBContainer.start();
  }

  @Autowired private WebApplicationContext webApplicationContext;

  @Autowired private MongoTemplate mongoTemplate;

  protected MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    mongoTemplate.dropCollection("transactions");
    mongoTemplate.dropCollection("import_jobs");
  }

  protected void awaitJobCompletion(String jobId) {
    Awaitility.await()
        .atMost(Duration.ofSeconds(10))
        .pollInterval(Duration.ofMillis(100))
        .untilAsserted(
            () ->
                mockMvc
                    .perform(get("/api/v1/imports/{jobId}/status", jobId))
                    .andExpect(status().isOk())
                    .andExpect(
                        jsonPath("$.status")
                            .value(
                                org.hamcrest.Matchers.anyOf(
                                    org.hamcrest.Matchers.is("COMPLETED"),
                                    org.hamcrest.Matchers.is("FAILED")))));
  }
}
