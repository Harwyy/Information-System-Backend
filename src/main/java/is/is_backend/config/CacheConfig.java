package is.is_backend.config;

import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    @Primary
    public CacheManager ehCacheManager() {
        CachingProvider provider = Caching.getCachingProvider();
        CacheManager cacheManager = provider.getCacheManager();

        createCacheIfAbsent(cacheManager, "coordinates", 500, Duration.ofMinutes(30));
        createCacheIfAbsent(cacheManager, "addresses", 100, Duration.ofMinutes(30));
        createCacheIfAbsent(cacheManager, "locations", 50, Duration.ofMinutes(30));
        createCacheIfAbsent(cacheManager, "organizations", 50, Duration.ofMinutes(30));
        createCacheIfAbsent(cacheManager, "org.hibernate.cache.internal.StandardQueryCache", 100, Duration.ofMinutes(30));
        createCacheIfAbsent(cacheManager, "org.hibernate.cache.spi.UpdateTimestampsCache", 100, Duration.ofMinutes(30));

        return cacheManager;
    }

    private void createCacheIfAbsent(CacheManager cacheManager, String cacheName, int heapEntries, Duration ttl) {
        if (cacheManager.getCache(cacheName) == null) {
            CacheConfiguration<Object, Object> config = CacheConfigurationBuilder
                    .newCacheConfigurationBuilder(
                            Object.class,
                            Object.class,
                            ResourcePoolsBuilder.heap(heapEntries)
                    )
                    .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(ttl))
                    .build();

            cacheManager.createCache(cacheName, Eh107Configuration.fromEhcacheCacheConfiguration(config));
        }
    }
}