package panda.orderservices.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
public class OrderSQSConfig {

    @Bean
    public SqsAsyncClient sqsAsyncClient(AwsCredentialsProvider awsCredentialsProvider) {
        return SqsAsyncClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

    @Primary
    @Bean
    public SqsTemplate sqsTemplate(SqsAsyncClient sqsAsyncClient) {
        return SqsTemplate.builder().sqsAsyncClient(sqsAsyncClient).build();
    }
}