package com.alphaStore.alphaS3.entity

import com.alphaStore.alphaS3.entity.superentity.SuperEntityWithIdCreatedLastModifiedDataStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Lob
import jakarta.persistence.Table

@Entity
@Table(name = "media_data")
data class MediaData(
    @Column(nullable = false)
    var mediaName: String,
    @Column(nullable = false)
    var mediaType: String,
    @Lob
    @Column(nullable = false)
    var imageData: ByteArray,
    @Column(nullable = false)
    var requestedFromMicroService: String,
    @Column(nullable = true)
    var purpose: String,
) : SuperEntityWithIdCreatedLastModifiedDataStatus() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MediaData

        if (mediaName != other.mediaName) return false
        if (mediaType != other.mediaType) return false
        if (!imageData.contentEquals(other.imageData)) return false
        if (requestedFromMicroService != other.requestedFromMicroService) return false
        if (purpose != other.purpose) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mediaName.hashCode()
        result = 31 * result + mediaType.hashCode()
        result = 31 * result + imageData.contentHashCode()
        result = 31 * result + requestedFromMicroService.hashCode()
        result = 31 * result + purpose.hashCode()
        return result
    }
}
