package com.blog.writeapi.configs.hibernate;

import com.blog.writeapi.configs.interceptors.QueryCountInterceptor;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return properties -> properties.put(
                "hibernate.session_factory.statement_inspector",
                new QueryCountInterceptor()
        );
    }
}