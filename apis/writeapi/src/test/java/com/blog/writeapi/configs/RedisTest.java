package com.blog.writeapi.configs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainerConfig.class)
class RedisTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void shouldUseRedis() {
        redisTemplate.opsForValue().set("test", "123");

        String value = redisTemplate.opsForValue().get("test");

        assertThat(value).isEqualTo("123");
    }
}
