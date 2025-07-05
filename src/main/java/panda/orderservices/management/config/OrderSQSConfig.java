package panda.orderservices.management.config;

import java.util.List;

import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.listener.QueueMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.PayloadMethodArgumentResolver;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;



@Configuration
public class OrderSQSConfig {
	
	public SqsAsyncClient sqsAsyncClient(AwsBasicCredentials awsBasicCredentials) {
		return SqsAsyncClient.builder()
				.region(Region.EU_CENTRAL_1)
				.credentialsProvider(StaticCredentialsProvider
						.create(awsBasicCredentials))
				.build();
	}
	
	@Primary
	@Bean
	public SqsTemplate sqsTemplate(SqsAsyncClient sqsAsyncClient) {
		return SqsTemplate.builder().sqsAsyncClient(sqsAsyncClient).build();
	}



}
