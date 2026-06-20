package com.ssafy.lumiroom.batch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LegalDongBatchController {

    private final JobLauncher jobLauncher;

    @Qualifier("legalDongImportJob")
    private final Job legalDongImportJob;

    @GetMapping("/batch/legal-dongs")
    public String runLegalDongBatch() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(legalDongImportJob, jobParameters);

        return "국토교통부 법정동코드 CSV 배치 작업 시작";
    }
}
