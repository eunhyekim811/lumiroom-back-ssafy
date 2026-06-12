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
public class SecurityFacilityBatchController {

    private final JobLauncher jobLauncher;

    @Qualifier("securityFacilityImportJob")
    private final Job securityFacilityImportJob;

    @GetMapping("/batch/securityFacility")
    public String runSecurityFacilityBatch() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(securityFacilityImportJob, jobParameters);
            return "치안시설 API 적재 배치 작업 시작";
        } catch (Exception e) {
            return "배치 실행 실패: " + e.getMessage();
        }
    }
}
