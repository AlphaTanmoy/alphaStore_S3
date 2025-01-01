package com.alphaStore.alphaS3.model.minified

import java.time.Instant

interface MediaDataMinified {
    var id: String
    var mediaName: String
    var mediaType: String
    var imageData: ByteArray
    var requestedFromMicroService: String
    var ownerId: String
    var createdDate: Instant
}