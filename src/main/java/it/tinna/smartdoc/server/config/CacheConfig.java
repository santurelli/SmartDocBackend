package it.tinna.smartdoc.server.config;

import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.Collection;
import java.util.stream.Collectors;

@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Default configuration for Redis cache, including TTL (Time To Live).
     */
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1)) // Durata predefinita: 1 ora
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    /**
     * Custom CacheResolver that prepends the company (dbName) to the cache names.
     * This ensures that cache entries are isolated between different clients/companies,
     * allowing for per-company eviction (allEntries = true).
     */
    @Bean("companyCacheResolver")
    public CacheResolver cacheResolver(CacheManager cacheManager) {
        return new SimpleCacheResolver(cacheManager) {
            @Override
            protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
                String dbName = DatabaseContextHolder.getClientDatabase();
                if (dbName == null) {
                    dbName = "global";
                }
                final String companyPrefix = dbName;
                Collection<String> cacheNames = super.getCacheNames(context);
                return cacheNames.stream()
                        .map(name -> companyPrefix + ":" + name)
                        .collect(Collectors.toList());
            }
        };
    }
}
