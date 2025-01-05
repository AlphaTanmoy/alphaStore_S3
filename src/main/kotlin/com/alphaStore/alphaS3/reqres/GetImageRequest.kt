package com.alphaStore.alphaS3.reqres

data class GetImageRequest (
    var name: String = "",
    var imageLink: String = "",
    var microServiceName: String = ""
)