package com.benko.integration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
@ConfigurationProperties(prefix = "integration")
public class ApiProperties {

    private URI baseUrl;
    private String developerId;
    private String buyEndpoint;
    private String allEndpoint;
    private String consumeEndpoint;
    private long timeoutMillis;

    public URI getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(URI baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(String developerId) {
        this.developerId = developerId;
    }

    public String getBuyEndpoint() {
        return buyEndpoint;
    }

    public void setBuyEndpoint(String buyEndpoint) {
        this.buyEndpoint = buyEndpoint;
    }

    public String getAllEndpoint() {
        return allEndpoint;
    }

    public void setAllEndpoint(String allEndpoint) {
        this.allEndpoint = allEndpoint;
    }

    public String getConsumeEndpoint() {
        return consumeEndpoint;
    }

    public void setConsumeEndpoint(String consumeEndpoint) {
        this.consumeEndpoint = consumeEndpoint;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }
}

