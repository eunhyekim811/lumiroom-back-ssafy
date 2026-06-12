package com.ssafy.lumiroom.batch.config;

import java.nio.charset.Charset;

import com.ssafy.lumiroom.batch.domain.RealEstateTradeInfo;
import com.ssafy.lumiroom.batch.dto.RealEstateTradeDto;
import com.ssafy.lumiroom.batch.processor.RealEstateTradeItemProcessor;
import com.ssafy.lumiroom.batch.reader.RealEstateTradeCsvReader;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class RealEstateTradeBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SqlSessionFactory sqlSessionFactory;
    private final RealEstateTradeItemProcessor realEstateTradeItemProcessor;

    @Value("${real-estate-trade.csv-pattern:file:data/*.csv}")
    private String csvPattern;

    @Value("${real-estate-trade.csv-encoding:CP949}")
    private String csvEncoding;

    @Bean
    public Job realEstateTradeImportJob() {
        return new JobBuilder("realEstateTradeImportJob", jobRepository)
                .start(realEstateTradeImportStep())
                .build();
    }

    @Bean
    public Step realEstateTradeImportStep() {
        return new StepBuilder("realEstateTradeImportStep", jobRepository)
                .<RealEstateTradeDto, RealEstateTradeInfo>chunk(1000, transactionManager)
                .reader(realEstateTradeCsvReader())
                .processor(realEstateTradeItemProcessor)
                .writer(realEstateTradeMyBatisWriter())
                .build();
    }

    @Bean
    public RealEstateTradeCsvReader realEstateTradeCsvReader() {
        return new RealEstateTradeCsvReader(csvPattern, Charset.forName(csvEncoding));
    }

    @Bean
    public MyBatisBatchItemWriter<RealEstateTradeInfo> realEstateTradeMyBatisWriter() {
        return new MyBatisBatchItemWriterBuilder<RealEstateTradeInfo>()
                .sqlSessionFactory(sqlSessionFactory)
                .statementId("com.ssafy.lumiroom.batch.mapper.RealEstateTradeMapper.upsertRealEstateTrade")
                .build();
    }
}
