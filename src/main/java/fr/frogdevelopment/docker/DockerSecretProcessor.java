package fr.frogdevelopment.docker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

@Component
public class DockerSecretProcessor implements EnvironmentPostProcessor, ApplicationListener<ContextRefreshedEvent> {

    private static final DeferredLog logger = new DeferredLog();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.switchTo(DockerSecretProcessor.class);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        var pathValue = environment.getProperty("docker-secrets.path", "/run/secrets");
        var path = Paths.get(pathValue);

        if (!Files.exists(path)) {
            logger.warn(format("Docker Secrets directory [%s] doesn't exist. Skipping loading.", pathValue));
            return;
        }

        if (!Files.isDirectory(path)) {
            logger.error(format("Docker Secrets directory [%s] is not a directory! Skipping loading.", pathValue));
            return;
        }

        try (var files = Files.list(path)) {
            var secrets = readingSecrets(files);

            var propertySource = new MapPropertySource("docker-secrets", secrets);

            environment.getPropertySources().addLast(propertySource);
        } catch (IOException e) {
            logger.error(format("Error while listing Docker Secrets in [%s]", pathValue), e);
        }
    }

    private Map<String, Object> readingSecrets(Stream<Path> files) {
        return files.collect(Collectors.toMap(
                path -> "docker-secrets." + path.getFileName().toString(),
                this::readingSecret
        ));
    }

    private Object readingSecret(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            logger.error(format("Unreadable Docker Secret [%s], replacing it with empty string",
                    path.getFileName().toString()), e);
            return "";
        }
    }

}
