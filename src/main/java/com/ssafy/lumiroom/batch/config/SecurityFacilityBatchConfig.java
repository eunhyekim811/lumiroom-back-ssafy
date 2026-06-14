package com.ssafy.lumiroom.batch.config;

import com.ssafy.lumiroom.batch.domain.SecurityFacilityInfo;
import com.ssafy.lumiroom.batch.dto.SecurityFacilityDto;
import com.ssafy.lumiroom.batch.processor.SecurityFacilityItemProcessor;
import com.ssafy.lumiroom.batch.reader.SecurityFacilityApiReader;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class SecurityFacilityBatchConfig {

    private static final int START_PAGE = 1;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SqlSessionFactory sqlSessionFactory;
    private final SecurityFacilityItemProcessor securityFacilityItemProcessor;

    @Value("${openapi.security-facility.base-url}")
    private String baseUrl;

    @Value("${openapi.security-facility.service-key}")
    private String serviceKey;

    @Value("${openapi.security-facility.num-of-rows}")
    private int numOfRows;

    @Bean
    public Job securityFacilityImportJob() {
        return new JobBuilder("securityFacilityImportJob", jobRepository)
                .start(securityFacilityImportStep())
                .build();
    }

    @Bean
    public Step securityFacilityImportStep() {
        return new StepBuilder("securityFacilityImportStep", jobRepository)
                .<SecurityFacilityDto, SecurityFacilityInfo>chunk(numOfRows, transactionManager)
                .reader(securityFacilityApiReader())
                .processor(securityFacilityItemProcessor)
                .writer(securityFacilityMyBatisWriter())
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(1000)
                .build();
    }

    @Bean
    public SecurityFacilityApiReader securityFacilityApiReader() {
        return new SecurityFacilityApiReader(baseUrl, serviceKey, START_PAGE, numOfRows);
    }

    @Bean
    public MyBatisBatchItemWriter<SecurityFacilityInfo> securityFacilityMyBatisWriter() {
        return new MyBatisBatchItemWriterBuilder<SecurityFacilityInfo>()
                .sqlSessionFactory(sqlSessionFactory)
                .statementId("com.ssafy.lumiroom.batch.mapper.SecurityFacilityMapper.upsertSecurityFacility")
                .build();
    }
}
