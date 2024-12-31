package com.alphaStore.alphaS3.error

class BadRequestExceptionThrowable(
    var errorMessage: String = ""
) : Throwable()