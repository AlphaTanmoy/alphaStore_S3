package com.alphaStore.alphaS3.entity.superentity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.io.Serializable
import java.time.ZonedDateTime
import java.util.*

@MappedSuperclass
abstract class SuperEntityWithIdCreatedLastModified (
    @Id
    var id: String = UUID.randomUUID().toString(),
    @LastModifiedDate
    @UpdateTimestamp
    var lastUpdated: ZonedDateTime = ZonedDateTime.now(),
    @CreatedDate
    @CreationTimestamp
    var createdDate: ZonedDateTime = ZonedDateTime.now(),
) : Serializable {

        fun <T : SuperEntityWithIdCreatedLastModifiedDataStatus> createDeepCopy(
            instanceToCopyTo: SuperEntityWithIdCreatedLastModifiedDataStatus
        ): T {
            @Suppress("UNCHECKED_CAST")
            return instanceToCopyTo.apply {
                id = this@SuperEntityWithIdCreatedLastModified.id
                lastUpdated = this@SuperEntityWithIdCreatedLastModified.lastUpdated
                createdDate = this@SuperEntityWithIdCreatedLastModified.createdDate
            } as T
        }
    }