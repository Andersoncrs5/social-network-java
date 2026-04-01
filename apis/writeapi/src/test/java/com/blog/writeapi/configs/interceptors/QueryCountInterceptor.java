package com.blog.writeapi.configs.interceptors;

import org.hibernate.resource.jdbc.spi.StatementInspector;

public class QueryCountInterceptor implements StatementInspector {

    private int count = 0;

    @Override
    public String inspect(String sql) {
        count++;
        return sql;
    }

    public int getCount() {
        return this.count;
    }

    public void reset() {
        this.count = 0;
    }
}