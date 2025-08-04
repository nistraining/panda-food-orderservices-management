package panda.orderservices.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;

@Configuration
public class CloudWatchConfig {

    @Bean
    public CloudWatchLogsClient cloudWatchConfigClient(AwsCredentialsProvider awsCredentialsProvider) {
        return CloudWatchLogsClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

}