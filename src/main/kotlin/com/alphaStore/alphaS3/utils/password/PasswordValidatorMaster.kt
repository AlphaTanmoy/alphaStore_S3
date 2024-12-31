package com.alphaStore.alphaS3.utils.password

import com.alphaStore.config_server.KeywordsAndConstants.PASSWORD_MIN_LENGTH
import com.alphaStore.config_server.KeywordsAndConstants.PASSWORD_MUST_HAVE_CAPITAL_LETTER
import com.alphaStore.config_server.KeywordsAndConstants.PASSWORD_MUST_HAVE_NUMBER
import com.alphaStore.config_server.KeywordsAndConstants.PASSWORD_MUST_HAVE_SMALL_LETTER
import com.alphaStore.config_server.KeywordsAndConstants.PASSWORD_MUST_HAVE_SPECIAL_CHAR
import com.alphaStore.alphaS3.error.BadRequestException
import org.springframework.stereotype.Component

@Component
class PasswordValidatorMaster {

    fun validate(passwordString: String, throwExceptionIfFailed: Boolean = true): Boolean {
        var valid = true
        var passwordValidationMessage = ""
        var genericMessageAdded = false
        if (
            PASSWORD_MIN_LENGTH != 0 &&
            passwordString.length < PASSWORD_MIN_LENGTH
        ) {
            passwordValidationMessage = "Password minimum length must be ${PASSWORD_MIN_LENGTH}. "
            valid = false
        }
        if (
            PASSWORD_MUST_HAVE_CAPITAL_LETTER &&
            !passwordString.contains(Regex(".*[A-Z]+.*"))
        ) {
            passwordValidationMessage = "${passwordValidationMessage}Password must have at least one capital latter"
            valid = false
            genericMessageAdded = true
        }
        if (
            PASSWORD_MUST_HAVE_SMALL_LETTER &&
            !passwordString.contains(Regex(".*[a-z]+.*"))
        ) {
            passwordValidationMessage = if (genericMessageAdded)
                "$passwordValidationMessage, small letter"
            else
                "${passwordValidationMessage}Password must have at least one small latter"
            valid = false
            genericMessageAdded = true
        }
        if (
            PASSWORD_MUST_HAVE_NUMBER &&
            !passwordString.contains(Regex(".*\\d+.*"))
        ) {
            passwordValidationMessage = if (genericMessageAdded)
                "$passwordValidationMessage, number"
            else
                "${passwordValidationMessage}Password must have at least one number"
            valid = false
            genericMessageAdded = true
        }
        if (
            PASSWORD_MUST_HAVE_SPECIAL_CHAR &&
            !passwordString.contains(Regex(".*\\W+.*"))
        ) {
            passwordValidationMessage = if (genericMessageAdded)
                "$passwordValidationMessage, special character (@%+#)"
            else
                "${passwordValidationMessage}Password must have at least one special character (@%+#)"
            valid = false
            genericMessageAdded = true
        }

        if (!valid && throwExceptionIfFailed) {
            throw BadRequestException(passwordValidationMessage)
        }

        return valid
    }
}