package com.cdq.banking;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

class FullImportIntegrationTest extends BaseIntegrationTest {

  @Test
  void shouldImportSampleCsvFileAndReturnCorrectStatistics() throws Exception {
    byte[] fileContent = new ClassPathResource("test-transactions.csv").getContentAsByteArray();

    MockMultipartFile file =
        new MockMultipartFile("file", "transactions.csv", "text/csv", fileContent);

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
        .andExpect(jsonPath("$.totalRows").value(10))
        .andExpect(jsonPath("$.validRows").value(10))
        .andExpect(jsonPath("$.invalidRows").value(0))
        .andExpect(jsonPath("$.rowErrors").isEmpty());

    mockMvc
        .perform(
            post("/api/v1/statistics/search")
                .contentType("application/json")
                .content("{\"groupBy\":\"CATEGORY\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(9))
        .andExpect(jsonPath("$.totalElements").value(9));

    mockMvc
        .perform(
            post("/api/v1/statistics/search")
                .contentType("application/json")
                .content("{\"groupBy\":\"IBAN\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(3))
        .andExpect(jsonPath("$.totalElements").value(3));

    mockMvc
        .perform(
            post("/api/v1/statistics/search")
                .contentType("application/json")
                .content("{\"groupBy\":\"MONTH\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(6))
        .andExpect(jsonPath("$.totalElements").value(6));
  }
}
