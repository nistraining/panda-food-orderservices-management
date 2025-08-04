package panda.orderservices.management.config;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class SecretConfig {

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        try {
            // ✅ Step 1: Read bootstrap credentials from Docker secrets
            String bootstrapAccessKey = Files.readString(Paths.get("/run/secrets/aws_access")).trim();
            String bootstrapSecretKey = Files.readString(Paths.get("/run/secrets/aws_secret")).trim();

            System.out.println("🔐 Bootstrap Access Key: " + bootstrapAccessKey);
            System.out.println("🔐 Bootstrap Secret Key: " + bootstrapSecretKey);

            AwsBasicCredentials bootstrapCreds = AwsBasicCredentials.create(bootstrapAccessKey, bootstrapSecretKey);

            // ✅ Step 2: Use bootstrap credentials to access Secrets Manager
            SecretsManagerClient secretsClient = SecretsManagerClient.builder()
                    .region(Region.of("eu-central-1"))
                    .credentialsProvider(StaticCredentialsProvider.create(bootstrapCreds))
                    .build();

            GetSecretValueRequest request = GetSecretValueRequest.builder()
                    .secretId("pandafoodsCredentials")
                    .build();

            GetSecretValueResponse response = secretsClient.getSecretValue(request);
            String secretJson = response.secretString();

            System.out.println("📦 Secrets Manager Response: " + secretJson);

            JsonObject creds = new Gson().fromJson(secretJson, JsonObject.class);

            if (!creds.has("cloud.aws.credentials.access-key") || !creds.has("cloud.aws.credentials.secret-key")) {
                throw new RuntimeException("❌ Missing expected keys in Secrets Manager response");
            }

            String finalAccessKey = creds.get("cloud.aws.credentials.access-key").getAsString();
            String finalSecretKey = creds.get("cloud.aws.credentials.secret-key").getAsString();

            System.out.println("✅ Final Access Key: " + finalAccessKey);
            System.out.println("✅ Final Secret Key: " + finalSecretKey);

            AwsBasicCredentials finalCreds = AwsBasicCredentials.create(finalAccessKey, finalSecretKey);
            return StaticCredentialsProvider.create(finalCreds);

        } catch (IOException e) {
            System.err.println("❌ Failed to read AWS secrets from Docker secret files");
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.err.println("❌ Failed to load credentials from Secrets Manager");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Bean
    public SqsClient sqsClient(AwsCredentialsProvider awsCredentialsProvider) {
        return SqsClient.builder()
                .region(Region.of("eu-central-1"))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }
}