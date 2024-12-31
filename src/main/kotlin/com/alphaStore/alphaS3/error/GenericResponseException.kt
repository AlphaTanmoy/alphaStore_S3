package com.alphaStore.alphaS3.error

import com.alphaStore.alphaS3.enums.ResponseType

class GenericResponseException(
    var code: Int? = null,
    var type: String? = null,
    override var message: String = "",
    var responseType: ResponseType = ResponseType.SUCCESS,
) : RuntimeException()
