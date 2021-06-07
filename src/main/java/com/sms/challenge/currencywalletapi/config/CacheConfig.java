package com.sms.challenge.currencywalletapi.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The type Cache config.
 */
@Configuration
public class CacheConfig {

    /**
     * Cache manager cache manager.
     *
     * @return the cache manager
     */
    @Bean
    public CacheManager cacheManager() {
        Config config = new Config();
        MapConfig mapConfigCurrencies = new MapConfig("currencies");
        mapConfigCurrencies.setTimeToLiveSeconds(30);
        config.addMapConfig(mapConfigCurrencies);
        MapConfig mapConfigCurrency = new MapConfig("currency");
        mapConfigCurrency.setTimeToLiveSeconds(10);
        config.addMapConfig(mapConfigCurrency);
        return new HazelcastCacheManager(Hazelcast.newHazelcastInstance(config));
    }
}
