package com.cdq.banking;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MvcResult;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class StatisticsIntegrationTest extends BaseIntegrationTest {

  private static final String CSV =
      """
      iban,date,currency,category,amount
      DE89370400440532013000,2026-01-15,EUR,GROCERIES,45.99
      DE89370400440532013000,2026-01-20,EUR,GROCERIES,30.00
      DE89370400440532013000,2026-02-10,EUR,RENT,950.00
      PL61109010140000071219812874,2026-01-05,PLN,UTILITIES,120.50
      PL61109010140000071219812874,2026-05-10,PLN,GROCERIES,80.00
      PL61109010140000071219812874,2026-08-20,PLN,TRANSPORT,55.00
      """;

  @BeforeEach
  void importTestData() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile("file", "stats-test.csv", "text/csv", CSV.getBytes());

    MvcResult importResult =
        mockMvc
            .perform(multipart("/api/v1/imports").file(file))
            .andExpect(status().isAccepted())
            .andReturn();

    String jobId =
        com.jayway.jsonpath.JsonPath.read(
            importResult.getResponse().getContentAsString(), "$.jobId");

    awaitJobCompletion(jobId);
  }

  @Test
  void shouldReturnPagedCategoryStatistics() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/statistics/search")
                .contentType("application/json")
                .content("{\"groupBy\":\"CATEGORY\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(5))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(20))
        .andExpect(jsonPath("$.totalElements").value(5))
        .andExpect(jsonPath("$.totalPages").value(1));
  }

  @Test
  void shouldReturnPagedIbanStatistics() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/statistics/search")
                .contentType("application/json")
                .content("{\"groupBy\":\"IBAN\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.totalElements").value(2));
  }

  @Test
  void shouldReturnPagedMonthlyStatistics() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/statistics/search")
                .contentType("application/json")
                .content("{\"groupBy\":\"MONTH\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(5))
        .andExpect(jsonPath("$.totalElements").value(5));
  }

  @Test
  void shouldFilterStatisticsByIban() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/statistics/search")
                .contentType("application/json")
                .content("{\"groupBy\":\"CATEGORY\",\"iban\":\"DE89370400440532013000\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].currency").value("EUR"))
        .andExpect(jsonPath("$.totalElements").value(2));
  }

  @Test
  void shouldFilterStatisticsByMonthRange() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/statistics/search")
                .contentType("application/json")
                .content(
                    "{\"groupBy\":\"MONTH\",\"fromMonth\":\"2026-05\",\"toMonth\":\"2026-08\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.totalElements").value(2));
  }

  @Test
  void shouldFilterByIbanAndMonthRange() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/statistics/search")
                .contentType("application/json")
                .content(
                    "{\"groupBy\":\"CATEGORY\",\"iban\":\"PL61109010140000071219812874\",\"fromMonth\":\"2026-05\",\"toMonth\":\"2026-08\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].currency").value("PLN"))
        .andExpect(jsonPath("$.totalElements").value(2));
  }

  @Test
  void shouldReturnEmptyWhenNoMatchingTransactions() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/statistics/search")
                .contentType("application/json")
                .content("{\"groupBy\":\"CATEGORY\",\"iban\":\"GB29NWBK60161331926819\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(0))
        .andExpect(jsonPath("$.totalElements").value(0))
        .andExpect(jsonPath("$.totalPages").value(0));
  }

  @Test
  void shouldPaginateResults() throws Exception {
    // Page 0, size 2 — should return 2 of 5 category results
    mockMvc
        .perform(
            post("/api/v1/statistics/search")
                .contentType("application/json")
                .content("{\"groupBy\":\"CATEGORY\",\"page\":0,\"size\":2}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(2))
        .andExpect(jsonPath("$.totalElements").value(5))
        .andExpect(jsonPath("$.totalPages").value(3));

    // Page 1, size 2 — should return next 2
    mockMvc
        .perform(
            post("/api/v1/statistics/search")
                .contentType("application/json")
                .content("{\"groupBy\":\"CATEGORY\",\"page\":1,\"size\":2}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.page").value(1))
        .andExpect(jsonPath("$.totalElements").value(5));
  }

  @Test
  void shouldReturn400WhenFromMonthAfterToMonth() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/statistics/search")
                .contentType("application/json")
                .content(
                    "{\"groupBy\":\"CATEGORY\",\"fromMonth\":\"2026-08\",\"toMonth\":\"2026-05\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400WhenSizeExceedsMax() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/statistics/search")
                .contentType("application/json")
                .content("{\"groupBy\":\"CATEGORY\",\"size\":101}"))
        .andExpect(status().isBadRequest());
  }
}
