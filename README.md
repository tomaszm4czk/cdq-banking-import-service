# CDQ Banking — Transaction Import & Statistics API

REST API for importing bank account transactions from CSV files and presenting aggregated statistics with filtering and pagination.

## Architecture

**Hexagonal architecture (ports & adapters)**

```
com.cdq.banking/
├── importing/          # CSV import bounded context
│   ├── domain/         # Pure Java — Transaction, ImportJob, validation, ports
│   ├── service/        # Use cases (ImportTransactionsServiceImpl, ImportProcessor)
│   ├── adapter/        # REST controllers, MongoDB adapters, CSV parser
│   └── config/         # Spring configuration
├── statistics/         # Statistics bounded context
│   ├── domain/         # Read models (sealed StatisticsResult), ports, StatisticsFilter
│   ├── service/        # StatisticsService
│   └── adapter/        # REST controllers, MongoDB aggregation, aggregation documents
└── config/             # Global error handling, OpenAPI config
```

Domain layer is framework-agnostic — no Spring imports in domain models or ports.

## Domain Exploration

Banking transactions domain was explored with the following considerations:

- **Transaction** is the core aggregate — immutable after import. Contains IBAN (validated value object following ISO 13616 format), date, amount with currency (Money value object), and category.
- **ImportJob** tracks the lifecycle of a CSV upload (PENDING -> PROCESSING -> COMPLETED/FAILED) — allows clients to poll for status.
- **Categories** modeled as enum (GROCERIES, SALARY, UTILITIES, RENT, ENTERTAINMENT, TRANSPORT, HEALTH, EDUCATION, OTHER) — extensible by adding new enum values.
- **IBAN** is a value object with format validation (2-letter country code + 2 check digits + BBAN).
- **Money** encapsulates amount + currency to avoid primitive obsession.
- **StatisticsResult** is a sealed interface (CategoryStatistics, IbanStatistics, MonthlyStatistics) — type-safe domain model for aggregation results.
- Statistics are computed on-the-fly via MongoDB aggregation pipelines (currency-aware) with optional filtering by IBAN and month range, and paginated responses.

## Tech Stack

| Component  | Technology |
|------------|------------|
| Framework  | Spring Boot 4.0.6 |
| Language   | Java 25 |
| Database   | MongoDB 8.0.10 |
| Build      | Gradle 9.4 |
| API Docs   | Springdoc OpenAPI (Swagger UI) |
| CSV        | OpenCSV |
| Caching    | Caffeine (10 min TTL) |
| Monitoring | Micrometer + Prometheus |
| Migrations | Mongock |
| Testing    | JUnit 5 + Testcontainers + Awaitility |
| Container  | Docker Compose |

## Quick Start

### Prerequisites
- Docker & Docker Compose
- Java 25+ (or use Docker-only flow)

### Run with Docker Compose

```bash
docker compose up --build
```

Application starts at `http://localhost:8080`

### Run locally (dev)

```bash
# Start MongoDB
docker compose up mongodb -d

# Run application
./gradlew bootRun
```

## API Usage

### Swagger UI
Open `http://localhost:8080/swagger-ui` for interactive API documentation.

### Import transactions

```bash
curl -X POST http://localhost:8080/api/v1/imports \
  -F "file=@sample/transactions.csv"
```

Response (202 Accepted — processing is async):
```json
{"jobId": "abc-123", "message": "Import accepted, poll /api/v1/imports/abc-123/status for results"}
```

### Check import status

```bash
curl http://localhost:8080/api/v1/imports/{jobId}/status
```

Response:
```json
{
  "jobId": "abc-123",
  "filename": "transactions.csv",
  "status": "COMPLETED",
  "totalRows": 10,
  "validRows": 9,
  "invalidRows": 1,
  "rowErrors": [{"row": 5, "errors": ["Invalid IBAN format: XX123"]}],
  "resultAvailable": true,
  "errorMessage": null
}
```

### Search statistics

All statistics queries use a single endpoint with optional filters and pagination:

```bash
curl -X POST http://localhost:8080/api/v1/statistics/search \
  -H "Content-Type: application/json" \
  -d '{"groupBy": "CATEGORY"}'
```

Response:
```json
{
  "content": [
    {
      "group": "GROCERIES",
      "currency": "EUR",
      "transactionCount": 15,
      "totalAmount": 450.99,
      "averageAmount": 30.07,
      "minAmount": 5.50,
      "maxAmount": 120.00
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 9,
  "totalPages": 1
}
```

#### Grouping dimensions

| groupBy    | Groups by                  |
|------------|----------------------------|
| `CATEGORY` | Transaction category + currency |
| `IBAN`     | Account IBAN + currency    |
| `MONTH`    | Year-month + currency      |

#### Optional filters

| Field     | Description                        | Example        |
|-----------|------------------------------------|----------------|
| iban      | Filter by specific IBAN            | `"DE89370400440532013000"` |
| fromMonth | Start of month range (inclusive)   | `"2026-05"`    |
| toMonth   | End of month range (inclusive)     | `"2026-08"`    |
| page      | Page number (0-based, default: 0)  | `0`            |
| size      | Page size (1-100, default: 20)     | `10`           |

#### Example: filtered + paginated

```bash
curl -X POST http://localhost:8080/api/v1/statistics/search \
  -H "Content-Type: application/json" \
  -d '{
    "groupBy": "CATEGORY",
    "iban": "DE89370400440532013000",
    "fromMonth": "2026-01",
    "toMonth": "2026-06",
    "page": 0,
    "size": 10
  }'
```

## Running Tests

```bash
# All tests (requires Docker for Testcontainers)
./gradlew test
```

## Key Design Decisions

- **Async import** — file upload returns 202 immediately, processing runs on a dedicated thread pool. Client polls status endpoint.
- **Durable jobs** — ImportJob persisted in MongoDB before async dispatch. On app restart, stuck PENDING/PROCESSING jobs are automatically recovered (marked FAILED).
- **Idempotent recovery** — transactions deleted by importJobId before re-insert on recovery, preventing duplicates.
- **On-the-fly aggregation** — statistics computed dynamically via MongoDB aggregation pipelines with `$facet` for pagination. No pre-materialized views needed at this scale.
- **Currency-aware statistics** — all aggregations group by currency to avoid mixing EUR + PLN amounts.
- **Partial import** — invalid rows are skipped, valid rows imported. Row-level error details returned in status response.
- **Compound index** `{iban, date}` for efficient filtered statistics queries.
- **Sealed interface** `StatisticsResult` — type-safe domain model for statistics, mapped to DTOs only in the adapter layer.
