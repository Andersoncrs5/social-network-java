package com.blog.writeapi.configs;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainerConfig {

    @Bean
    @ServiceConnection
    public MySQLContainer<?> mysqlContainer() {
        return new MySQLContainer<>("mysql:8.0.33")
                .withDatabaseName("social_network_test_db")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true);
    }

    @Bean
    @ServiceConnection
    public RedisContainer redisContainer() {
        return new RedisContainer(DockerImageName.parse("redis:7.2")).withReuse(true);
    }

    @Container
    static KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("apache/kafka-native:latest"));

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

}