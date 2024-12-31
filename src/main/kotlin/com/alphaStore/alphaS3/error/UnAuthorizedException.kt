package com.alphaStore.alphaS3.error

class UnAuthorizedException(
    var errorMessage: String = "",
    var code: Int? = null
) : RuntimeException()