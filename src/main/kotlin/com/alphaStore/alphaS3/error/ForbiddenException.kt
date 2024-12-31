package com.alphaStore.alphaS3.error

class ForbiddenException(
    var errorMessage: String = "",
    var code: Int? = null
) : RuntimeException()