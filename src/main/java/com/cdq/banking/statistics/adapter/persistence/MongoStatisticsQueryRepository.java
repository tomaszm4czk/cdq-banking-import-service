package com.cdq.banking.statistics.adapter.persistence;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import com.cdq.banking.statistics.adapter.persistence.document.*;
import com.cdq.banking.statistics.domain.model.*;
import com.cdq.banking.statistics.domain.port.StatisticsQueryRepository;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MongoStatisticsQueryRepository implements StatisticsQueryRepository {

  private static final String TRANSACTIONS = "transactions";

  private final MongoTemplate mongoTemplate;

  @Override
  public PagedResult<StatisticsResult> findByCategory(StatisticsFilter filter) {
    List<AggregationOperation> stages = new ArrayList<>(buildMatchStage(filter));
    stages.add(buildStandardGroup(Fields.fields().and("category").and("currency")));
    stages.add(sort(Sort.Direction.ASC, "_id.category", "_id.currency"));

    return executePaged(
        stages,
        filter,
        CategoryAggregationResult.class,
        results ->
            results.stream()
                .<StatisticsResult>map(
                    r ->
                        new CategoryStatistics(
                            r.id().category(),
                            r.id().currency(),
                            r.transactionCount(),
                            r.totalAmount(),
                            r.averageAmount(),
                            r.minAmount(),
                            r.maxAmount()))
                .toList());
  }

  @Override
  public PagedResult<StatisticsResult> findByIban(StatisticsFilter filter) {
    List<AggregationOperation> stages = new ArrayList<>(buildMatchStage(filter));
    stages.add(buildStandardGroup(Fields.fields().and("iban").and("currency")));
    stages.add(sort(Sort.Direction.ASC, "_id.iban", "_id.currency"));

    return executePaged(
        stages,
        filter,
        IbanAggregationResult.class,
        results ->
            results.stream()
                .<StatisticsResult>map(
                    r ->
                        new IbanStatistics(
                            r.id().iban(),
                            r.id().currency(),
                            r.transactionCount(),
                            r.totalAmount(),
                            r.averageAmount(),
                            r.minAmount(),
                            r.maxAmount()))
                .toList());
  }

  @Override
  public PagedResult<StatisticsResult> findByMonth(StatisticsFilter filter) {
    List<AggregationOperation> stages = new ArrayList<>(buildMatchStage(filter));
    stages.add(
        project()
            .andExpression("year(date)")
            .as("year")
            .andExpression("month(date)")
            .as("month")
            .and("amount")
            .as("amount")
            .and("currency")
            .as("currency"));
    stages.add(buildStandardGroup(Fields.fields().and("year").and("month").and("currency")));
    stages.add(sort(Sort.Direction.ASC, "_id.year", "_id.month", "_id.currency"));

    return executePaged(
        stages,
        filter,
        MonthlyAggregationResult.class,
        results ->
            results.stream()
                .<StatisticsResult>map(
                    r ->
                        new MonthlyStatistics(
                            r.id().year(),
                            r.id().month(),
                            r.id().currency(),
                            r.transactionCount(),
                            r.totalAmount(),
                            r.averageAmount(),
                            r.minAmount(),
                            r.maxAmount()))
                .toList());
  }

  private <A, T> PagedResult<T> executePaged(
      List<AggregationOperation> stages,
      StatisticsFilter filter,
      Class<A> aggResultType,
      java.util.function.Function<List<A>, List<T>> mapper) {

    int skip = filter.page() * filter.size();

    FacetOperation facet =
        facet()
            .and(
                context -> new Document("$skip", skip),
                context -> new Document("$limit", filter.size()))
            .as("data")
            .and(context -> new Document("$count", "count"))
            .as("totalCount");

    List<AggregationOperation> withFacet = new ArrayList<>(stages);
    withFacet.add(facet);

    AggregationResults<Document> facetResults =
        mongoTemplate.aggregate(newAggregation(withFacet), TRANSACTIONS, Document.class);

    Document result = facetResults.getUniqueMappedResult();
    if (result == null) {
      return PagedResult.of(List.of(), filter.page(), filter.size(), 0);
    }

    List<Document> totalCountList = result.getList("totalCount", Document.class);
    long totalElements =
        totalCountList.isEmpty() ? 0 : totalCountList.getFirst().getInteger("count", 0);

    List<Document> dataDocs = result.getList("data", Document.class);
    List<A> aggResults =
        dataDocs.stream()
            .map(doc -> mongoTemplate.getConverter().read(aggResultType, doc))
            .toList();

    List<T> content = mapper.apply(aggResults);
    return PagedResult.of(content, filter.page(), filter.size(), totalElements);
  }

  private GroupOperation buildStandardGroup(Fields groupFields) {
    return group(groupFields)
        .count()
        .as("transactionCount")
        .sum("amount")
        .as("totalAmount")
        .avg("amount")
        .as("averageAmount")
        .min("amount")
        .as("minAmount")
        .max("amount")
        .as("maxAmount");
  }

  private List<AggregationOperation> buildMatchStage(StatisticsFilter filter) {
    List<Criteria> criteria = new ArrayList<>();

    Optional.ofNullable(filter.iban())
        .ifPresent(iban -> criteria.add(Criteria.where("iban").is(iban)));
    Optional.ofNullable(filter.fromMonth())
        .map(month -> month.atDay(1))
        .ifPresent(from -> criteria.add(Criteria.where("date").gte(from)));
    Optional.ofNullable(filter.toMonth())
        .map(YearMonth::atEndOfMonth)
        .ifPresent(to -> criteria.add(Criteria.where("date").lte(to)));

    if (criteria.isEmpty()) {
      return List.of();
    }
    return List.of(match(new Criteria().andOperator(criteria.toArray(new Criteria[0]))));
  }
}
