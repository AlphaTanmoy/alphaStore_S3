package com.alphaStore.alphaS3.error

class UnAuthorizedExceptionThrowable(
    var errorMessage: String = "",
    var code: Int? = null
) : Throwable()