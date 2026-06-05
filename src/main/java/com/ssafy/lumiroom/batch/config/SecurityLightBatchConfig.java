package com.ssafy.lumiroom.batch.config;

import com.ssafy.lumiroom.batch.domain.SecurityLightInfo;
import com.ssafy.lumiroom.batch.dto.SecurityLightDto;
import com.ssafy.lumiroom.batch.processor.SecurityLightItemProcessor;
import com.ssafy.lumiroom.batch.reader.SecurityLightApiReader;
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
public class SecurityLightBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SqlSessionFactory sqlSessionFactory;
    private final SecurityLightItemProcessor itemProcessor;

    @Value("${openapi.security-light.base-url}")
    private String baseUrl;

    @Value("${openapi.security-light.service-key}")
    private String serviceKey;

    @Bean
    public Job securityLightJob() {
        return new JobBuilder("securityLightJob", jobRepository)
                .start(securityLightStep())
                .build();
    }

    @Bean
    public Step securityLightStep() {
        return new StepBuilder("securityLightStep", jobRepository)
                .<SecurityLightDto, SecurityLightInfo>chunk(1000, transactionManager)
                .reader(securityLightApiReader())
                .processor(itemProcessor)
                .writer(securityLightMyBatisWriter())
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(1000)
                .build();
    }

    @Bean
    public SecurityLightApiReader securityLightApiReader() {
        // 커스텀 API 리더기 빈 등록
        return new SecurityLightApiReader(baseUrl, serviceKey, 831);
    }

    @Bean
    public MyBatisBatchItemWriter<SecurityLightInfo> securityLightMyBatisWriter() {
        return new MyBatisBatchItemWriterBuilder<SecurityLightInfo>()
                .sqlSessionFactory(sqlSessionFactory)
                .statementId("com.ssafy.lumiroom.batch.mapper.SecurityLightMapper.upsertSecurityLight")
                .build();
    }
}