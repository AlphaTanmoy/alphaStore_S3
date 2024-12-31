package com.alphaStore.alphaS3.utils

import com.alphaStore.alphaS3.error.BadRequestException
import java.util.*

class ValidateForUUID {
    companion object {
        fun check(potentialId: String, paramName: String = "", customErrorMessage: String? = null): Boolean {
            try {
                UUID.fromString(potentialId)
            } catch (ex: Exception) {
                ex.printStackTrace()
                val paramPostFix = if (paramName == "") "" else " "
                val errorMessageToShow = customErrorMessage ?: run {
                    "Please provide a valid ${paramName}${paramPostFix}id"
                }
                throw BadRequestException(errorMessageToShow)
            }
            return true
        }
    }
}