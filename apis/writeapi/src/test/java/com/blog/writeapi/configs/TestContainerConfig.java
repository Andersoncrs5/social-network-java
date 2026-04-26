package com.blog.writeapi.configs;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
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

//    // 1. Mudamos para estático para garantir que inicie antes do contexto
//    private static final KafkaContainer kafkaContainer = new KafkaContainer(
//            DockerImageName.parse("confluentinc/cp-kafka:7.6.1")
//                    .asCompatibleSubstituteFor("apache/kafka"))
//            .withEnv("KAFKA_NODE_ID", "1")
//            .withEnv("KAFKA_PROCESS_ROLES", "broker,controller")
//            .withEnv("KAFKA_LISTENERS", "PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093")
//            .withEnv("KAFKA_ADVERTISED_LISTENERS", "PLAINTEXT://0.0.0.0:9092")
//            .withEnv("KAFKA_CONTROLLER_QUORUM_VOTERS", "1@localhost:9093")
//            .withEnv("KAFKA_CONTROLLER_LISTENER_NAMES", "CONTROLLER");
//
//    static {
//        kafkaContainer.start();
//    }
//
//    // 2. Usamos o DynamicPropertyRegistry para injetar manualmente
//    @DynamicPropertySource
//    static void kafkaProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
//    }
}