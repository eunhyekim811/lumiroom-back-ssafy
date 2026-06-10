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
public class CctvBatchController {

    private final JobLauncher jobLauncher;
    private final Job cctvImportJob; // 우리가 Config에서 만든 바로 그 Job

    @GetMapping("/batch/cctv")
    public String runCctvBatch() throws Exception {

        // 배치가 실행될 때마다 고유한 파라미터(현재 시간)를 주어
        // "이미 실행된 배치입니다"라는 에러를 방지합니다.
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        // 잡 런처를 통해 배치 시작!
        jobLauncher.run(cctvImportJob, jobParameters);

        return "CCTV 배치 작업 시작";
    }
}