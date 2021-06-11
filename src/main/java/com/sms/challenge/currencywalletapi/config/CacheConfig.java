package com.sms.challenge.currencywalletapi.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NetworkConfig;
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
        MapConfig mapConfigConversion = new MapConfig("conversion");
        mapConfigConversion.setTimeToLiveSeconds(10);
        config.addMapConfig(mapConfigConversion);

        // By default, Hazelcast uses multicast for discovering other members that can form a cluster.
        // If multicast isn't a preferred way of discovery for our environment, then we can configure Hazelcast for a full TCP/IP cluster.
        NetworkConfig network = config.getNetworkConfig();
        network.setPort(5701).setPortCount(20);
        network.setPortAutoIncrement(true);
        JoinConfig join = network.getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig().addMember("localhost").setEnabled(true);

        return new HazelcastCacheManager(Hazelcast.newHazelcastInstance(config));
    }
}
