package panda.orderservices.management.config;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@Configuration
public class SecretConfig {

    @Bean
    public AwsBasicCredentials awsBasicCredentials() {
        try {
            // ✅ Step 1: Read bootstrap credentials from Docker secrets
            String bootstrapAccessKey = Files.readString(Paths.get("/run/secrets/aws_access")).trim();
            String bootstrapSecretKey = Files.readString(Paths.get("/run/secrets/aws_secret")).trim();

            AwsBasicCredentials bootstrapCreds = AwsBasicCredentials.create(bootstrapAccessKey, bootstrapSecretKey);

            // ✅ Step 2: Use bootstrap credentials to access Secrets Manager
            SecretsManagerClient secretsClient = SecretsManagerClient.builder()
                    .region(Region.of("eu-central-1"))
                    .credentialsProvider(StaticCredentialsProvider.create(bootstrapCreds))
                    .build();

            GetSecretValueRequest request = GetSecretValueRequest.builder()
                    .secretId("pandafoodsCredentials") // Update with your actual secret name if needed
                    .build();

            GetSecretValueResponse response = secretsClient.getSecretValue(request);
            JsonObject creds = new Gson().fromJson(response.secretString(), JsonObject.class);

            // ✅ Step 3: Return the final AWS credentials fetched from Secrets Manager
            String finalAccessKey = creds.get("cloud.aws.credentials.access-key").getAsString();
            String finalSecretKey = creds.get("cloud.aws.credentials.secret-key").getAsString();

            return AwsBasicCredentials.create(finalAccessKey, finalSecretKey);

        } catch (IOException e) {
            throw new RuntimeException("❌ Failed to read AWS secrets from Docker secret files", e);
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to load credentials from Secrets Manager", e);
        }
    }
}