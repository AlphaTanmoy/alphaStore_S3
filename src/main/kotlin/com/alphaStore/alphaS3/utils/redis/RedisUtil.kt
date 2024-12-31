package com.alphaStore.alphaS3.utils.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.HashOperations
import java.util.*

class RedisUtil {

    inline fun <reified T> getObjectFromRedis(
        redisHashOperation: HashOperations<String, String, String>,
        key: String,
        hash: String
    ): T {
        return ObjectMapper()
            .setTimeZone(TimeZone.getDefault())
            .findAndRegisterModules()
            .readValue(
                redisHashOperation
                    .get(
                        key,
                        hash
                    ),
                T::class.java
            )
    }

    fun getStringFromRedis(
        redisHashOperation: HashOperations<String, String, String>,
        key: String,
        hash: String
    ): String {
        return redisHashOperation
            .get(
                key,
                hash
            ) ?: ""
    }

    inline fun <reified T> getObjectListFromRedis(
        redisHashOperation: HashOperations<String, String, String>,
        key: String,
        hash: String
    ): ArrayList<T> {
        return ObjectMapper()
            .setTimeZone(TimeZone.getDefault())
            .findAndRegisterModules()
            .readValue(
                redisHashOperation
                    .get(
                        key,
                        hash
                    ),
                ObjectMapper()
                    .setTimeZone(TimeZone.getDefault())
                    .typeFactory
                    .constructCollectionType(
                        ArrayList::class.java,
                        T::class.java
                    )
            )
    }

    inline fun <reified T> getObjectFromRedisString(
        redisString: String
    ): T {
        return ObjectMapper()
            .setTimeZone(TimeZone.getDefault())
            .findAndRegisterModules()
            .readValue(
                redisString,
                T::class.java
            )
    }

    inline fun <reified T> getObjectListFromRedisString(
        redisString: String
    ): ArrayList<T> {
        return ObjectMapper()
            .setTimeZone(TimeZone.getDefault())
            .findAndRegisterModules()
            .readValue(
                redisString,
                ObjectMapper()
                    .setTimeZone(TimeZone.getDefault())
                    .typeFactory
                    .constructCollectionType(
                        ArrayList::class.java,
                        T::class.java
                    )
            )
    }

    fun saveToRedis(
        redisHashOperation: HashOperations<String, String, String>,
        key: String,
        hash: String,
        stringToSave: String
    ) {
        redisHashOperation.put(
            key,
            hash,
            stringToSave
        )
    }

}