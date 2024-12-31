package com.alphaStore.alphaS3.model

import com.fasterxml.jackson.annotation.JsonFilter
import java.io.Serializable

data class TokenCreationResponse(
    var token: String = "",
    var refreshToken: String = "",
) : Serializable

@JsonFilter("tokenCreationResponseFilter")
class TokenCreationResponseMixIn