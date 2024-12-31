package com.alphaStore.alphaS3.entity.superentity

import com.alphaStore.alphaS3.enums.DataStatus
import jakarta.persistence.*
import java.io.Serializable
import java.util.*


@MappedSuperclass
abstract class SuperEntityWithIdDataStatus (
    @Id
    var id: String = UUID.randomUUID().toString(),
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var dataStatus: DataStatus = DataStatus.ACTIVE,
): Serializable {

    fun <T : SuperEntityWithIdDataStatus> createDeepCopy(
        instanceToCopyTo: SuperEntityWithIdDataStatus
    ): T {
        @Suppress("UNCHECKED_CAST")
        return instanceToCopyTo.apply {
            id = this@SuperEntityWithIdDataStatus.id
            dataStatus = this@SuperEntityWithIdDataStatus.dataStatus
        } as T
    }
}