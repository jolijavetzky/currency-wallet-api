package com.sms.challenge.currencywalletapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * The type App config.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "config")
public class AppConfig {

    private String cryptoCompareApiBaseUrl;
}
