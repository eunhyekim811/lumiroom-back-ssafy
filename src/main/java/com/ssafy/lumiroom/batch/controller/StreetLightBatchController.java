package com.ssafy.lumiroom.batch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StreetLightBatchController {

    private final JobLauncher jobLauncher;
    private final Job streetLightJob;

    @GetMapping("/batch/streetLight")
    public String runStreetLightBatch() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(streetLightJob, jobParameters);
            return "가로등 API 적재 배치 작업 시작";
        } catch (Exception e) {
            return "배치 수동 가동 중 예외 발생: " + e.getMessage();
        }
    }
}