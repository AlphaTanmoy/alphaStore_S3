package com.alphaStore.alphaS3.error

class ForbiddenExceptionThrowable(
    var errorMessage: String = "",
    var code: Int? = null
) : Throwable()