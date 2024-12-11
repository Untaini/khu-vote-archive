package com.example.khuvote.vote.schedule;

import lombok.RequiredArgsConstructor;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class QuartzBatchJob extends QuartzJobBean {

    private final JobLauncher jobLauncher;
    private final ApplicationContext applicationContext;

    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

            String jobName = jobDataMap.getString("votingJob");
            Map<String, Job> jobMap = (Map<String, Job>) applicationContext.getBean("jobMap");
            Job job = jobMap.get(jobName);

            String deployKey = jobDataMap.getString("deployKey");
            String contractAddress = jobDataMap.getString("contractAddress");

            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .addString("deployKey", deployKey)
                    .addString("contractAddress", contractAddress)
                    .toJobParameters();

            jobLauncher.run(job, params);
        } catch (Exception e) {
            JobExecutionException jobExecutionException = new JobExecutionException(e);
            jobExecutionException.setRefireImmediately(true);
            throw jobExecutionException;
        }
    }
}
