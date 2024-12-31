package com.alphaStore.alphaS3.utils.redis

import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class RedisKeyUtil(
    private val environment: Environment
) {

    fun getKey(keyFor: String): String {
        if (environment.activeProfiles.isEmpty()) {
            return "${keyFor}-loc"
        }
        return "$keyFor-${environment.activeProfiles[0]}"
    }
}