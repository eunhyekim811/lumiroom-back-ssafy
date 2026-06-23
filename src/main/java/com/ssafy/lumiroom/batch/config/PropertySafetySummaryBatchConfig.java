package com.ssafy.lumiroom.batch.config;

import com.ssafy.lumiroom.batch.domain.PropertySafetySummary;
import com.ssafy.lumiroom.batch.mapper.PropertySafetySummaryMapper;
import com.ssafy.lumiroom.batch.processor.PropertySafetySummaryProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class PropertySafetySummaryBatchConfig {

    private static final String MAPPER_NAMESPACE =
            "com.ssafy.lumiroom.batch.mapper.PropertySafetySummaryMapper.";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SqlSessionFactory sqlSessionFactory;

    @Bean
    public Job propertySafetySummaryJob(
            @Qualifier("propertySafetySummaryStep") Step propertySafetySummaryStep
    ) {
        return new JobBuilder("propertySafetySummaryJob", jobRepository)
                .start(propertySafetySummaryStep)
                .build();
    }

    @Bean
    public Step propertySafetySummaryStep(
            MyBatisPagingItemReader<Long> propertySafetyPropertyIdReader,
            PropertySafetySummaryProcessor propertySafetySummaryProcessor,
            ItemWriter<PropertySafetySummary> propertySafetySummaryWriter,
            @Value("${batch.property-safety.chunk-size:100}") int chunkSize
    ) {
        return new StepBuilder("propertySafetySummaryStep", jobRepository)
                .<Long, PropertySafetySummary>chunk(chunkSize, transactionManager)
                .reader(propertySafetyPropertyIdReader)
                .processor(propertySafetySummaryProcessor)
                .writer(propertySafetySummaryWriter)
                .build();
    }

    @Bean
    @StepScope
    public MyBatisPagingItemReader<Long> propertySafetyPropertyIdReader(
            @Value("${batch.property-safety.page-size:100}") int pageSize
    ) {
        return new MyBatisPagingItemReaderBuilder<Long>()
                .sqlSessionFactory(sqlSessionFactory)
                .queryId(MAPPER_NAMESPACE + "findTargetPropertyIds")
                .pageSize(pageSize)
                .saveState(true)
                .build();
    }

    @Bean
    @StepScope
    public PropertySafetySummaryProcessor propertySafetySummaryProcessor(
            @Value("${batch.property-safety.radius-m:1000}") int radiusM,
            @Value("#{jobParameters['calculationVersion']}") String calculationVersion,
            @Value("${batch.property-safety.score.cctv-target-count:10}") int cctvTargetCount,
            @Value("${batch.property-safety.score.security-light-target-count:20}") int securityLightTargetCount,
            @Value("${batch.property-safety.score.security-facility-target-count:3}") int securityFacilityTargetCount,
            @Value("${batch.property-safety.score.cctv-weight:40}") BigDecimal cctvWeight,
            @Value("${batch.property-safety.score.security-light-weight:30}") BigDecimal securityLightWeight,
            @Value("${batch.property-safety.score.security-facility-weight:30}") BigDecimal securityFacilityWeight
    ) {
        PropertySafetySummaryMapper batchMapper =
                new SqlSessionTemplate(sqlSessionFactory, ExecutorType.BATCH)
                        .getMapper(PropertySafetySummaryMapper.class);
        return new PropertySafetySummaryProcessor(
                batchMapper,
                radiusM,
                calculationVersion,
                cctvTargetCount,
                securityLightTargetCount,
                securityFacilityTargetCount,
                cctvWeight,
                securityLightWeight,
                securityFacilityWeight
        );
    }

    @Bean
    public ItemWriter<PropertySafetySummary> propertySafetySummaryWriter() {
        PropertySafetySummaryMapper batchMapper =
                new SqlSessionTemplate(sqlSessionFactory, ExecutorType.BATCH)
                        .getMapper(PropertySafetySummaryMapper.class);
        return chunk -> {
            for (PropertySafetySummary summary : chunk) {
                batchMapper.upsertPropertySafetySummary(summary);
            }
        };
    }
}
