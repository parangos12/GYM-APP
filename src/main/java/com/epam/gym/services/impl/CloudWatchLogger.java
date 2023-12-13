package com.epam.gym.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;
import java.time.LocalDate;

import java.time.Instant;
import java.util.Collections;

@Service
public class CloudWatchLogger {

    private static final Logger logger = LoggerFactory.getLogger(CloudWatchLogger.class);

    private final CloudWatchLogsClient cloudWatchLogsClient;
    private final String logGroupName = "/ecs/TraineeMicroservice";

    @Autowired
    public CloudWatchLogger(CloudWatchLogsClient cloudWatchLogsClient) {
        this.cloudWatchLogsClient = cloudWatchLogsClient;
    }

    public void logToCloudWatch(String message) {
        String logStreamName = "ecs-task-" + LocalDate.now();

        if (!logStreamExists(logStreamName)) {
            createLogStream(logStreamName);
        }

        InputLogEvent logEvent = InputLogEvent.builder()
                .message(message)
                .timestamp(Instant.now().toEpochMilli())
                .build();

        PutLogEventsRequest putLogEventsRequest = PutLogEventsRequest.builder()
                .logGroupName(logGroupName)
                .logStreamName(logStreamName)
                .logEvents(Collections.singletonList(logEvent))
                .build();

        try {
            PutLogEventsResponse response = cloudWatchLogsClient.putLogEvents(putLogEventsRequest);

            if (response.sdkHttpResponse().statusCode() == 200) {
                logger.info("Log sent to CloudWatch Logs successfully");
            } else {
                logger.error("Failed to send log to CloudWatch Logs. Response code: {}", response.sdkHttpResponse().statusCode());
            }
        } catch (Exception e) {
            logger.error("Error sending log to CloudWatch Logs", e);
        }
    }

    private boolean logStreamExists(String logStreamName) {
        DescribeLogStreamsRequest describeLogStreamsRequest = DescribeLogStreamsRequest.builder()
                .logGroupName(logGroupName)
                .logStreamNamePrefix(logStreamName)
                .build();

        DescribeLogStreamsResponse describeLogStreamsResponse = cloudWatchLogsClient.describeLogStreams(describeLogStreamsRequest);

        return !describeLogStreamsResponse.logStreams().isEmpty();
    }

    private void createLogStream(String logStreamName) {
        CreateLogStreamRequest createLogStreamRequest = CreateLogStreamRequest.builder()
                .logGroupName(logGroupName)
                .logStreamName(logStreamName)
                .build();

        cloudWatchLogsClient.createLogStream(createLogStreamRequest);
    }
}
