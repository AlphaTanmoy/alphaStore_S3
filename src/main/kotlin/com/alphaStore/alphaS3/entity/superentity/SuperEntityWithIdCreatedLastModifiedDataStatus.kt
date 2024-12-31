package com.alphaStore.alphaS3.entity.superentity

import com.alphaStore.alphaS3.enums.DataStatus
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.io.Serializable
import java.time.ZonedDateTime
import java.util.*

@MappedSuperclass
abstract class SuperEntityWithIdCreatedLastModifiedDataStatus(
    @Id var id: String = UUID.randomUUID().toString(),
    @LastModifiedDate
    @UpdateTimestamp
    var lastUpdated: ZonedDateTime = ZonedDateTime.now(),
    @CreatedDate
    @CreationTimestamp
    var createdDate: ZonedDateTime = ZonedDateTime.now(),
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var dataStatus: DataStatus = DataStatus.ACTIVE,
) : Serializable {

    fun <T : SuperEntityWithIdCreatedLastModifiedDataStatus> createDeepCopy(
        instanceToCopyTo: SuperEntityWithIdCreatedLastModifiedDataStatus
    ): T {
        @Suppress("UNCHECKED_CAST")
        return instanceToCopyTo.apply {
            id = this@SuperEntityWithIdCreatedLastModifiedDataStatus.id
            lastUpdated = this@SuperEntityWithIdCreatedLastModifiedDataStatus.lastUpdated
            createdDate = this@SuperEntityWithIdCreatedLastModifiedDataStatus.createdDate
            dataStatus = this@SuperEntityWithIdCreatedLastModifiedDataStatus.dataStatus
        } as T
    }
}