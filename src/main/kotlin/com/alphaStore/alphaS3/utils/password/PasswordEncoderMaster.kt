package com.alphaStore.alphaS3.utils.password

import com.alphaStore.config_server.KeywordsAndConstants.PASSWORD_CONFIG_BCRYPT_ROUND
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordEncoderMaster {

    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder(PASSWORD_CONFIG_BCRYPT_ROUND)
    }

    fun matches(passwordToCheck: String, hashToCheck: String): Boolean {
        return passwordEncoder().matches(passwordToCheck, hashToCheck)
    }

    fun encode(passwordToEncode: String): String {
        return passwordEncoder().encode(passwordToEncode)
    }
}