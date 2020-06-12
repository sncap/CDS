package com.cds;

import java.time.Duration;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;

@Configuration
public class RedisConfiguration {
//	@Bean
//	public ClientOptions clientOptions() {
//		return ClientOptions.builder().timeoutOptions(TimeoutOptions.enabled(Duration.ofSeconds(5))).build();
//	}

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
			      .clientOptions(
			          ClientOptions.builder()
			              .socketOptions(
			                  SocketOptions.builder()
			                      .connectTimeout(Duration.ofMillis(10000))
			                      .build())
			              .build())
			      .commandTimeout(Duration.ofSeconds(60)) //Command timed out after 10 second(s)
			      .build();
		
		LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory( redisStandaloneConfiguration, clientConfiguration);
		return lettuceConnectionFactory;
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(List.class));
		return redisTemplate;
	}

}