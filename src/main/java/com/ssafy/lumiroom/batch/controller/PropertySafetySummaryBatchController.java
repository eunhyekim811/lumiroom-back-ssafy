package com.ssafy.lumiroom.batch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch/property-safety-summary")
@RequiredArgsConstructor
public class PropertySafetySummaryBatchController {

    private final JobLauncher jobLauncher;

    @Qualifier("propertySafetySummaryJob")
    private final Job propertySafetySummaryJob;

    @PostMapping
    public String run(
            @RequestParam(defaultValue = "v1") String calculationVersion,
            @RequestParam(required = false) Long runId
    ) throws Exception {
        JobParametersBuilder parameters = new JobParametersBuilder()
                .addString("calculationVersion", calculationVersion);

        if (runId != null) {
            parameters.addLong("runId", runId);
        }

        JobExecution execution = jobLauncher.run(propertySafetySummaryJob, parameters.toJobParameters());
        return "propertySafetySummaryJob started: executionId="
                + execution.getId() + ", status=" + execution.getStatus();
    }
}
