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
public class SecurityLightBatchController {

    private final JobLauncher jobLauncher;
    private final Job securityLightJob; // Config에서 만든 @Bean 메서드 이름과 일치해야 합니다.

    @GetMapping("/batch/securityLight")
    public String runSecurityLightBatch() {
        try {
            // 스프링 배치는 동일한 파라미터로 재실행이 안 되므로,
            // 매번 실행이 가능하도록 현재 시간(타임스탬프)을 파라미터로 넘겨줍니다.
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(securityLightJob, jobParameters);
            return "보안등 API 적재 배치 작업 시작";
        } catch (Exception e) {
            return "배치 실행 실패: " + e.getMessage();
        }
    }
}