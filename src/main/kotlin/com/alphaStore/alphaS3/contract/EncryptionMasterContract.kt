package com.alphaStore.alphaS3.contract


interface EncryptionMasterContract {

    fun encrypt(
        stringToEncrypt: String,
    ): String

    fun encrypt(
        stringToEncrypt: String,
        password: String,
        salt: String? = null
    ): String

    fun decrypt(
        toDecrypt: String? = null,
        tryOne: Boolean = true,
        tryTwo: Boolean = false,
        tryThree: Boolean = false,
    ): String

    fun decryptWithPassword(
        toDecrypt: String,
        password: String = "",
        salt: String? = null
    ): String
}