package com.blog.writeapi.configs.snowflakeid;

import cn.hutool.core.lang.Snowflake;
import org.springframework.stereotype.Component;

@Component
public class SnowflakeContext {
    private static Snowflake generator;

    public SnowflakeContext(Snowflake generator) {
        SnowflakeContext.generator = generator;
    }

    public static Long nextId() {
        return generator.nextId();
    }
}