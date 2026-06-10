package com.ssafy.lumiroom.batch.config;

import com.ssafy.lumiroom.batch.domain.CctvInfo;
import com.ssafy.lumiroom.batch.dto.CctvDto;
import com.ssafy.lumiroom.batch.processor.CctvItemProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class CctvBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SqlSessionFactory sqlSessionFactory;
    private final CctvItemProcessor cctvItemProcessor;

    @Bean
    public Job cctvImportJob() {   // 배치 작업의 최상위(Job 설정)
        return new JobBuilder("cctvImportJob", jobRepository)
                .start(cctvImportStep())
                .build();
    }

    @Bean
    public Step cctvImportStep() {
        return new StepBuilder("cctvImportStep", jobRepository)
                .<CctvDto, CctvInfo>chunk(1000, transactionManager) // 1000건씩 처리
                .reader(cctvCsvReader())
                .processor(cctvItemProcessor)
                .writer(cctvMyBatisWriter())
                .build();
    }

    @Bean
    public FlatFileItemReader<CctvDto> cctvCsvReader() {
        return new FlatFileItemReaderBuilder<CctvDto>()
                .name("cctvCsvReader")
                // 프로젝트 최상단(root)에 위치한 CSV 파일 로드
                .resource(new FileSystemResource("data/CCTV_info.csv"))
                .encoding("CP949")
                .linesToSkip(1) // 첫 줄 헤더 건너뛰기
                .delimited()
                .strict(false)
                .names(
                        "openGovCode", "managementNo", "agencyName", "roadAddress", "lotAddress",
                        "purpose", "cameraCount", "pixels", "direction", "storageDays",
                        "installYm", "phone", "lat", "lon", "baseDate",
                        "updateType", "updateTime", "lastModifiedTime"
                )
                .targetType(CctvDto.class)
                .build();
    }

    @Bean
    public MyBatisBatchItemWriter<CctvInfo> cctvMyBatisWriter() {
        return new MyBatisBatchItemWriterBuilder<CctvInfo>()
                .sqlSessionFactory(sqlSessionFactory)
                .statementId("com.ssafy.lumiroom.batch.mapper.CctvMapper.upsertCctv")
                .build();
    }
}