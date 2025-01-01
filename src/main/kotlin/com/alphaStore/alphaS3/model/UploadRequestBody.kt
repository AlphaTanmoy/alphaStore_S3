package com.alphaStore.alphaS3.model

import org.springframework.web.multipart.MultipartFile

data class UploadRequestBody (
    var imageData: MultipartFile,
    var requestedFromMicroService: String,
    var purpose: String? = "Default"
)