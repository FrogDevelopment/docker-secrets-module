package fr.frogdevelopment.docker;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(classes = DockerSecretProcessor.class)
public class DockerSecretProcessorTest {

    @Autowired
    private Environment environment;

    @BeforeAll
    static void createSecrets() throws IOException {
        var secretsPath = Paths.get(System.getProperty("java.io.tmpdir"), "secrets");
        if (Files.exists(secretsPath)) {
            Files.list(secretsPath).forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Files.delete(secretsPath);
        }

        Files.createDirectory(secretsPath);
        var absolutePath = secretsPath.toFile().getAbsolutePath();
        System.out.println(absolutePath);

        var myTest = Files.createFile(Paths.get(absolutePath, "my-test"));
        Files.writeString(myTest, "my value");

        var otherTest = Files.createFile(Paths.get(absolutePath, "other_test"));
        Files.writeString(otherTest, "other_value");
    }

    @Test
    void contextLoads() {

        assertThat(environment.containsProperty("docker-secrets.my-test")).isTrue();
        assertThat(environment.getProperty("docker-secrets.my-test")).isEqualTo("my value");

        assertThat(environment.containsProperty("docker-secrets.other_test")).isTrue();
        assertThat(environment.getProperty("docker-secrets.other_test")).isEqualTo("other_value");
    }

}
