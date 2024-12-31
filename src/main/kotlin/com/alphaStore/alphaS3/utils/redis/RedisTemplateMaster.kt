package com.alphaStore.alphaS3.utils.redis

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.stereotype.Component

@Component
@Lazy
class RedisTemplateMaster(
    private val redisCredentials: RedisCredentials
) {

    @Bean
    @Lazy
    fun jedisConnectionFactory(configs: RedisCredentials): JedisConnectionFactory {
        val conf = RedisStandaloneConfiguration(
            redisCredentials.REDIS_HOST,
            redisCredentials.REDIS_PORT
        )
        conf.setPassword(redisCredentials.REDIS_PASSWORD)
        return JedisConnectionFactory(conf)
    }

    @Bean
    @Lazy
    fun redisTemplate(jedisConnectionFactory: JedisConnectionFactory): RedisTemplate<String, String> {
        val redisTemplate: RedisTemplate<String, String> = RedisTemplate<String, String>()
        redisTemplate.connectionFactory = jedisConnectionFactory
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = StringRedisSerializer()
        redisTemplate.hashValueSerializer = StringRedisSerializer()
        redisTemplate.hashKeySerializer = StringRedisSerializer()
        return redisTemplate
    }
}