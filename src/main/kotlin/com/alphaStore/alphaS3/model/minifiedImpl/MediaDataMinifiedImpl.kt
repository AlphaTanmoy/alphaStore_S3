package com.alphaStore.alphaS3.model.minifiedImpl

import com.alphaStore.alphaS3.model.minified.MediaDataMinified
import java.time.Instant

data class MediaDataMinifiedImpl(
    override var id: String,
    override var mediaName: String,
    override var mediaType: String,
    override var imageData: ByteArray,
    override var requestedFromMicroService: String,
    override var ownerId: String,
    override var createdDate: Instant
): MediaDataMinified {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MediaDataMinifiedImpl

        if (mediaName != other.mediaName) return false
        if (mediaType != other.mediaType) return false
        if (ownerId != other.ownerId) return false
        if (!imageData.contentEquals(other.imageData)) return false
        if (requestedFromMicroService != other.requestedFromMicroService) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mediaName.hashCode()
        result = 31 * result + mediaType.hashCode()
        result = 31 * result + ownerId.hashCode()
        result = 31 * result + imageData.contentHashCode()
        result = 31 * result + requestedFromMicroService.hashCode()
        return result
    }
}
