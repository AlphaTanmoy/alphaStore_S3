package com.alphaStore.alphaS3.error

class GenericException(
    var errorMessage: String = "",
    var code: Int = 0
) : RuntimeException()