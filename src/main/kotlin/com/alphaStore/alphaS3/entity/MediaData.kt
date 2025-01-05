package com.alphaStore.alphaS3.entity

import com.alphaStore.alphaS3.entity.superentity.SuperEntityWithIdCreatedLastModifiedDataStatus
import com.alphaStore.alphaS3.enums.MediaExtentions
import com.alphaStore.alphaS3.enums.MediaType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "media_data")
data class MediaData(
    @Column(nullable = false)
    var actualName: String,
    @Column(nullable = false)
    var mediaExtentions: MediaExtentions = MediaExtentions.PNG,
    @Enumerated
    var mediaType: MediaType = MediaType.IMAGE,
    @Column(nullable = false)
    var imageData: String,
    @Column(nullable = false)
    var requestedFromMicroService: String,
    @Column(nullable = true)
    var driveId: String,
) : SuperEntityWithIdCreatedLastModifiedDataStatus()
