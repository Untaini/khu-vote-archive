package com.example.khuvote.vote.schedule;

import com.example.khuvote.vote.dto.VotingProcessDTO;
import com.example.khuvote.vote.service.VotingProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class VoteBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final VotingProcessService votingProcessService;

    @Bean
    public Map<String, Job> jobMap() {
        Map<String, Job> jobMap = new HashMap<>();
        jobMap.put("startVotingJob", startVotingJob());
        jobMap.put("endVotingJob", endVotingJob());

        return jobMap;
    }

    public Job startVotingJob() {
        Job job = new JobBuilder("startVotingJob", jobRepository)
                .start(startVotingStep())
                .build();

        return job;
    }

    public Step startVotingStep() {
        Step step = new StepBuilder("startVotingStep", jobRepository)
                .tasklet(startVotingTasklet(), transactionManager)
                .build();

        return step;
    }

    public Tasklet startVotingTasklet() {
        return ((contribution, chunkContext) -> {
            String deployKey = chunkContext.getStepContext().getJobParameters()
                    .get("deployKey").toString();

            String contractAddress = chunkContext.getStepContext().getJobParameters()
                    .get("contractAddress").toString();

            VotingProcessDTO.StartRequest request = VotingProcessDTO.StartRequest.builder()
                    .deployKey(deployKey)
                    .contractAddress(contractAddress)
                    .build();

            try {
                retryTemplate().execute(context -> votingProcessService.startVoting(request));
            } catch (Exception e) {
                log.error(String.format("[Contract: %s] startVoting Error", request.contractAddress()));
            }

            return RepeatStatus.FINISHED;
        });
    }

    public Job endVotingJob() {
        Job job = new JobBuilder("endVotingJob", jobRepository)
                .start(endVotingStep())
                .build();

        return job;
    }

    public Step endVotingStep() {
        Step step = new StepBuilder("endVotingStep", jobRepository)
                .tasklet(endVotingTasklet(), transactionManager)
                .build();

        return step;
    }

    public Tasklet endVotingTasklet() {
        return ((contribution, chunkContext) -> {
            String deployKey = chunkContext.getStepContext().getJobParameters()
                    .get("deployKey").toString();

            String contractAddress = chunkContext.getStepContext().getJobParameters()
                    .get("contractAddress").toString();

            VotingProcessDTO.EndRequest request = VotingProcessDTO.EndRequest.builder()
                    .deployKey(deployKey)
                    .contractAddress(contractAddress)
                    .build();

            try {
                retryTemplate().execute(context -> votingProcessService.endVoting(request));
            } catch (Exception e) {
                log.error(String.format("[Contract: %s] endVoting Error", request.contractAddress()));
            }

            return RepeatStatus.FINISHED;
        });
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(Integer.MAX_VALUE);

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(500);

        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }
}
