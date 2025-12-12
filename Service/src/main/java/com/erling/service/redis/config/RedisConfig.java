package com.erling.service.redis.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    // 配置第一个 Redis 数据库连接
    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory0() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("localhost");
        config.setPort(6379);
        config.setDatabase(0); // 数据库索引
        return new LettuceConnectionFactory(config);
    }
    // 配置第二个 Redis 数据库连接
    @Bean
    public RedisConnectionFactory redisConnectionFactory1() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("localhost");
        config.setPort(6379);
        config.setDatabase(1); // 使用不同的数据库索引
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory2() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("localhost");
        config.setPort(6379);
        config.setDatabase(2); // 使用不同的数据库索引
        return new LettuceConnectionFactory(config);
    }

    // 为第一个数据库创建 RedisTemplate
    @Bean
    @Primary
    public RedisTemplate<String, String> redisTemplate0(
            @Qualifier("redisConnectionFactory0") RedisConnectionFactory connectionFactory) {
        return getStringStringRedisTemplate(connectionFactory);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate1(
            @Qualifier("redisConnectionFactory1") RedisConnectionFactory connectionFactory) {
        return getStringStringRedisTemplate(connectionFactory);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate2(
            @Qualifier("redisConnectionFactory2") RedisConnectionFactory connectionFactory) {
        return getStringStringRedisTemplate(connectionFactory);
    }

    private RedisTemplate<String, String> getStringStringRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

//    @Bean
//    public RedisTemplate<String,String> redisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String,String> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(new StringRedisSerializer());
//        template.setHashKeySerializer(new StringRedisSerializer());
//        template.setHashValueSerializer(new StringRedisSerializer());
//        template.afterPropertiesSet();
//        return template;
//    }
}
