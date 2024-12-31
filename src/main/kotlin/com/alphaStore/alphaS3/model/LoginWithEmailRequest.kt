package com.alphaStore.alphaS3.model

data class LoginWithEmailRequest(
    var emailId: String = "",
    var password: String = "",
)
