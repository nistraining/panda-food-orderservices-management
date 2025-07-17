package panda.orderservices.management.config;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DBPasswordInjectorConfig implements EnvironmentPostProcessor {

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		try {
            System.out.println("🧬 DBPasswordEnvironmentInjector activated");
            String dbPassword = Files.readString(Paths.get("/run/secrets/db_password")).trim();

            Map<String, Object> props = new HashMap<>();
            props.put("spring.datasource.password", dbPassword);
            environment.getPropertySources().addFirst(new MapPropertySource("dbSecretSource", props));

        } catch (Exception e) {
            System.err.println("❌ Failed to inject DB password: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

   
}