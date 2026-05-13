package com.cdq.banking.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@ChangeUnit(id = "v001-create-indexes", order = "001", author = "cdq")
public class V001_CreateIndexes {

  @Execution
  public void execute(MongoTemplate mongoTemplate) {
    mongoTemplate.indexOps("transactions").createIndex(new Index().on("iban", Sort.Direction.ASC));
    mongoTemplate.indexOps("transactions").createIndex(new Index().on("date", Sort.Direction.ASC));
    mongoTemplate
        .indexOps("transactions")
        .createIndex(
            new Index()
                .on("iban", Sort.Direction.ASC)
                .on("date", Sort.Direction.ASC)
                .named("iban_date"));
    mongoTemplate
        .indexOps("transactions")
        .createIndex(new Index().on("category", Sort.Direction.ASC));
    mongoTemplate
        .indexOps("transactions")
        .createIndex(new Index().on("importJobId", Sort.Direction.ASC));

    mongoTemplate.indexOps("import_jobs").createIndex(new Index().on("status", Sort.Direction.ASC));
  }

  @RollbackExecution
  public void rollback(MongoTemplate mongoTemplate) {
    mongoTemplate.indexOps("transactions").dropIndex("iban_1");
    mongoTemplate.indexOps("transactions").dropIndex("date_1");
    mongoTemplate.indexOps("transactions").dropIndex("iban_date");
    mongoTemplate.indexOps("transactions").dropIndex("category_1");
    mongoTemplate.indexOps("transactions").dropIndex("importJobId_1");
    mongoTemplate.indexOps("import_jobs").dropIndex("status_1");
  }
}
