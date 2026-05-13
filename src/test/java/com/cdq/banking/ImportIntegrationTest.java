package com.cdq.banking;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

class ImportIntegrationTest extends BaseIntegrationTest {

  @Test
  void shouldImportCsvAndCheckStatus() throws Exception {
    String csv =
        """
                iban,date,currency,category,amount
                DE89370400440532013000,2026-01-15,EUR,GROCERIES,45.99
                PL61109010140000071219812874,2026-01-20,PLN,SALARY,3500.00
                """;

    MockMultipartFile file =
        new MockMultipartFile("file", "transactions.csv", "text/csv", csv.getBytes());

    MvcResult importResult =
        mockMvc
            .perform(multipart("/api/v1/imports").file(file))
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.jobId").isNotEmpty())
            .andReturn();

    String jobId =
        com.jayway.jsonpath.JsonPath.read(
            importResult.getResponse().getContentAsString(), "$.jobId");

    awaitJobCompletion(jobId);

    mockMvc
        .perform(get("/api/v1/imports/{jobId}/status", jobId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("COMPLETED"))
        .andExpect(jsonPath("$.totalRows").value(2))
        .andExpect(jsonPath("$.validRows").value(2))
        .andExpect(jsonPath("$.invalidRows").value(0))
        .andExpect(jsonPath("$.resultAvailable").value(true));
  }

  @Test
  void shouldReportInvalidRows() throws Exception {
    String csv =
        """
                iban,date,currency,category,amount
                DE89370400440532013000,2026-01-15,EUR,GROCERIES,45.99
                BAD_IBAN,not-a-date,FAKE,NOPE,abc
                """;

    MockMultipartFile file = new MockMultipartFile("file", "mixed.csv", "text/csv", csv.getBytes());

    MvcResult importResult =
        mockMvc
            .perform(multipart("/api/v1/imports").file(file))
            .andExpect(status().isAccepted())
            .andReturn();

    String jobId =
        com.jayway.jsonpath.JsonPath.read(
            importResult.getResponse().getContentAsString(), "$.jobId");

    awaitJobCompletion(jobId);

    mockMvc
        .perform(get("/api/v1/imports/{jobId}/status", jobId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalRows").value(2))
        .andExpect(jsonPath("$.validRows").value(1))
        .andExpect(jsonPath("$.invalidRows").value(1));
  }

  @Test
  void shouldReturn404ForUnknownJob() throws Exception {
    mockMvc.perform(get("/api/v1/imports/nonexistent/status")).andExpect(status().isNotFound());
  }
}
