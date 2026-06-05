package com.ssafy.lumiroom.batch.config;

import com.ssafy.lumiroom.batch.domain.StreetLightInfo;
import com.ssafy.lumiroom.batch.dto.StreetLightDto;
import com.ssafy.lumiroom.batch.processor.StreetLightItemProcessor;
import com.ssafy.lumiroom.batch.reader.StreetLightApiReader;
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
public class StreetLightBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final StreetLightItemProcessor streetLightItemProcessor;
    private final SqlSessionFactory sqlSessionFactory;

    // 대용량 제어 파라미터를 자바 상수로 통합 (직관적인 제어)
    private static final int START_PAGE = 1;     // 중간에 튕기면 이 숫자만 수정하여 이어 달리기
    private static final int CHUNK_SIZE = 1000;  // 한 번에 가져올 데이터 건수

    @Value("${openapi.street-light.base-url}")
    private String baseUrl;

    @Value("${openapi.street-light.service-key}")
    private String serviceKey;

    @Bean
    public Job streetLightJob() {
        return new JobBuilder("streetLightJob", jobRepository)
                .start(streetLightStep())
                .build();
    }

    @Bean
    public Step streetLightStep() {
        return new StepBuilder("streetLightStep", jobRepository)
                .<StreetLightDto, StreetLightInfo>chunk(CHUNK_SIZE, transactionManager)
                .reader(streetLightApiReader())
                .processor(streetLightItemProcessor)
                .writer(streetLightWriter())
                // 간헐적 통신 장애 시 배치가 죽지 않도록 방어
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(1000)
                .build();
    }

    @Bean
    public StreetLightApiReader streetLightApiReader() {
        return new StreetLightApiReader(baseUrl, serviceKey, START_PAGE, CHUNK_SIZE);
    }

    @Bean
    public MyBatisBatchItemWriter<StreetLightInfo> streetLightWriter() {
        return new MyBatisBatchItemWriterBuilder<StreetLightInfo>()
                .sqlSessionFactory(sqlSessionFactory)
                .statementId("com.ssafy.lumiroom.batch.mapper.StreetLightMapper.upsertStreetLights")
                .build();
    }
}