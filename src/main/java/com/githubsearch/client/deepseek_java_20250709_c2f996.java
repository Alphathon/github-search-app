@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager(MeterRegistry meterRegistry) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .maximumSize(1000)
            .recordStats());
        
        // Register cache metrics
        CaffeineCacheMetrics.monitor(meterRegistry, cacheManager, "githubSearchCache");
        
        return cacheManager;
    }
}



