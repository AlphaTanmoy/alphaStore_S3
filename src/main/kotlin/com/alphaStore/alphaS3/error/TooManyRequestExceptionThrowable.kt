package com.alphaStore.alphaS3.error

class TooManyRequestExceptionThrowable(
    var errorMessage: String = "",
) : Throwable()