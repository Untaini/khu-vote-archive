package com.example.khuvote.vote.schedule;

import com.example.khuvote.vote.dto.ScheduleVoteDTO;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class VotingProcessScheduler {

    private final Scheduler scheduler;

    public void scheduleVote(ScheduleVoteDTO.Request request) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("deployKey", request.deployKey());
        jobDataMap.put("contractAddress", request.contractAddress());

        while (true) {
            try {
                addScheduledJob(jobDataMap, "startVotingJob", request.contractAddress(), request.startTime());

                break;
            } catch (Exception e) {}
        }

        while (true) {
            try {
                addScheduledJob(jobDataMap, "endVotingJob", request.contractAddress(), request.endTime());

                break;
            } catch (Exception e) {}
        }
    }

    private void addScheduledJob(JobDataMap defaultJobDataMap, String jobName, String jobGroup, Timestamp time)
            throws SchedulerException {

        defaultJobDataMap.put("votingJob", jobName);

        JobDetail jobDetail = JobBuilder.newJob(QuartzBatchJob.class)
                .withIdentity(jobName, jobGroup)
                .usingJobData(defaultJobDataMap)
                .storeDurably()
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .startAt(time)
                .forJob(jobDetail)
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }
}
