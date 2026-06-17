package com.ssafy.lumiroom.batch.config;

import java.nio.charset.Charset;

import com.ssafy.lumiroom.batch.domain.LegalDongInfo;
import com.ssafy.lumiroom.batch.dto.LegalDongDto;
import com.ssafy.lumiroom.batch.processor.LegalDongItemProcessor;
import com.ssafy.lumiroom.batch.reader.LegalDongCsvReader;
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
public class LegalDongBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SqlSessionFactory sqlSessionFactory;
    private final LegalDongItemProcessor legalDongItemProcessor;

    @Value("${legal-dong.csv-path:file:data/legal_dongs.csv}")
    private String csvPath;

    @Value("${legal-dong.csv-encoding:CP949}")
    private String csvEncoding;

    @Bean
    public Job legalDongImportJob() {
        return new JobBuilder("legalDongImportJob", jobRepository)
                .start(legalDongImportStep())
                .build();
    }

    @Bean
    public Step legalDongImportStep() {
        return new StepBuilder("legalDongImportStep", jobRepository)
                .<LegalDongDto, LegalDongInfo>chunk(1000, transactionManager)
                .reader(legalDongCsvReader())
                .processor(legalDongItemProcessor)
                .writer(legalDongMyBatisWriter())
                .build();
    }

    @Bean
    public LegalDongCsvReader legalDongCsvReader() {
        return new LegalDongCsvReader(csvPath, Charset.forName(csvEncoding));
    }

    @Bean
    public MyBatisBatchItemWriter<LegalDongInfo> legalDongMyBatisWriter() {
        return new MyBatisBatchItemWriterBuilder<LegalDongInfo>()
                .sqlSessionFactory(sqlSessionFactory)
                .statementId("com.ssafy.lumiroom.batch.mapper.LegalDongMapper.upsertLegalDong")
                .build();
    }
}
