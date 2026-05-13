package com.cdq.banking;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ActuatorIntegrationTest {

  @ServiceConnection static final MongoDBContainer mongoDBContainer;

  static {
    mongoDBContainer = new MongoDBContainer("mongo:8.0.10");
    mongoDBContainer.start();
  }

  @LocalServerPort private int port;

  private final HttpClient httpClient = HttpClient.newHttpClient();

  private HttpResponse<String> get(String path) throws Exception {
    HttpRequest request =
        HttpRequest.newBuilder().uri(URI.create("http://localhost:" + port + path)).GET().build();
    return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  }

  @Test
  void healthEndpointShouldReturnUpWithMongoDetails() throws Exception {
    HttpResponse<String> response = get("/actuator/health");

    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(response.body()).contains("\"status\":\"UP\"");
    assertThat(response.body()).contains("mongo");
  }

  @Test
  void prometheusEndpointShouldExposeMetrics() throws Exception {
    HttpResponse<String> response = get("/actuator/prometheus");

    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(response.body()).contains("jvm_memory");
    assertThat(response.body()).contains("import_jobs");
  }

  @Test
  void infoEndpointShouldBeAccessible() throws Exception {
    HttpResponse<String> response = get("/actuator/info");

    assertThat(response.statusCode()).isEqualTo(200);
  }
}
