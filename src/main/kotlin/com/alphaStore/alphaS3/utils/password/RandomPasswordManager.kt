package com.alphaStore.alphaS3.utils.password

import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
object RandomPasswordManager {

    fun getRandomPassword(): String {
        val CHARSET_AZ_09 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqustuvwxyz0123456789".toCharArray()
        var newPassword = ""
        for (i in 0..15) {
            newPassword = "$newPassword${CHARSET_AZ_09[Random.nextInt(CHARSET_AZ_09.size)]}"
        }
        return newPassword
    }
}