package com.blog.writeapi.configs;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest
public abstract class AbstractIntegrationTest {

    @Container
    static LocalStackContainer localStack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:4.4.0"))
                    .withServices(LocalStackContainer.Service.S3)
                    .withReuse(true);

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {

        registry.add("spring.cloud.aws.s3.endpoint",
                () -> localStack.getEndpointOverride(LocalStackContainer.Service.S3).toString());

        registry.add("spring.cloud.aws.credentials.access-key",
                localStack::getAccessKey);

        registry.add("spring.cloud.aws.credentials.secret-key",
                localStack::getSecretKey);

        registry.add("spring.cloud.aws.region.static",
                localStack::getRegion);
    }
}