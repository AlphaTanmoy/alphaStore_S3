package com.alphaStore.alphaS3.contract

interface EncodingUtilContract {

    fun encode(toEncode: String): String

    fun decode(toDecode: String): String
}