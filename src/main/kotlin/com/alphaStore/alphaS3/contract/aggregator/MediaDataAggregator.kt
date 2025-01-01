package com.alphaStore.alphaS3.contract.aggregator

import com.alphaStore.alphaS3.contract.repo.MediaDataRepo
import com.alphaStore.alphaS3.entity.MediaData
import com.alphaStore.alphaS3.model.minifiedImpl.FetchMostRecentMinifiedImpl
import org.springframework.stereotype.Component
import com.alphaStore.alphaS3.reqres.AggregatorListResponse
import com.alphaStore.alphaS3.reqres.AggregatorResponse
import com.alphaStore.alphaS3.model.minifiedImpl.MediaDataMinifiedImpl
import java.time.ZonedDateTime
import java.util.*

@Component
class MediaDataAggregator(
    private val mediaDataRepo: MediaDataRepo
) {

    fun save(entity: MediaData): MediaData {
        return mediaDataRepo.save(entity)
    }

    fun saveAll(entities: List<MediaData>) {
        mediaDataRepo.saveAll(entities)
    }

    fun findByName(name: String) : AggregatorListResponse<MediaDataMinifiedImpl> {
        val databaseAccessLogId = UUID.randomUUID().toString()
        val resultFromDb =
            mediaDataRepo.findByName(
                name = name
            ).map{ toMap->
                MediaDataMinifiedImpl(
                    mediaName = toMap.mediaName,
                    mediaType = toMap.mediaType,
                    imageData = toMap.imageData,
                    requestedFromMicroService = toMap.requestedFromMicroService,
                    ownerId = toMap.ownerId,
                    id = toMap.id,
                    createdDate = toMap.createdDate
                )
            }.toCollection(ArrayList())
        return AggregatorListResponse(data = resultFromDb, databaseAccessLogId)
    }

    fun findCountWithOutOffsetIdAndDate(
        mediaName: String,
        mediaType: String,
        ownerId: String,
        microServiceName: String
    ): AggregatorResponse<Long>{
        val databaseAccessLogId = UUID.randomUUID().toString()
        val resultFromDb =
            mediaDataRepo.findCountWithOutOffsetDateAndLimit(
                mediaName = mediaName,
                mediaType = mediaType,
                ownerId = ownerId,
                microServiceName = microServiceName
            )
        return AggregatorResponse(data = resultFromDb, databaseAccessLogId)
    }

    fun findDataWithOutOffsetIdAndDate(
        mediaName: String,
        mediaType: String,
        ownerId: String,
        microServiceName: String
    ): AggregatorListResponse<MediaDataMinifiedImpl>{
        val databaseAccessLogId = UUID.randomUUID().toString()
        val resultFromDb =
            mediaDataRepo.findDataWithOutOffsetDateAndLimit(
                mediaName = mediaName,
                mediaType = mediaType,
                ownerId = ownerId,
                microServiceName = microServiceName
            ).map{ toMap->
                MediaDataMinifiedImpl(
                    mediaName = toMap.mediaName,
                    mediaType = toMap.mediaType,
                    imageData = toMap.imageData,
                    requestedFromMicroService = toMap.requestedFromMicroService,
                    ownerId = toMap.ownerId,
                    id = toMap.id,
                    createdDate = toMap.createdDate
                )
            }.toCollection(ArrayList())
        return AggregatorListResponse(data = resultFromDb, databaseAccessLogId)
    }

    fun findDataWithOutOffsetId(
        mediaName: String,
        mediaType: String,
        ownerId: String,
        microServiceName: String,
        limit: Int,
        offsetDate: ZonedDateTime
    ): AggregatorListResponse<MediaDataMinifiedImpl>{
        val databaseAccessLogId = UUID.randomUUID().toString()
        val resultFromDb =
            mediaDataRepo.findDataWithOutOffsetId(
                mediaName = mediaName,
                mediaType = mediaType,
                ownerId = ownerId,
                microServiceName = microServiceName,
                limit = limit
            ).map{ toMap->
                MediaDataMinifiedImpl(
                    mediaName = toMap.mediaName,
                    mediaType = toMap.mediaType,
                    imageData = toMap.imageData,
                    requestedFromMicroService = toMap.requestedFromMicroService,
                    ownerId = toMap.ownerId,
                    id = toMap.id,
                    createdDate = toMap.createdDate
                )
            }.toCollection(ArrayList())
        return AggregatorListResponse(data = resultFromDb, databaseAccessLogId)
    }

    fun findDataWithOffsetId(
        mediaName: String,
        mediaType: String,
        ownerId: String,
        microServiceName: String,
        limit: Int,
        offsetId: String
    ): AggregatorListResponse<MediaDataMinifiedImpl>{
        val databaseAccessLogId = UUID.randomUUID().toString()
        val resultFromDb =
            mediaDataRepo.findDataWithOffsetId(
                mediaName = mediaName,
                mediaType = mediaType,
                ownerId = ownerId,
                microServiceName = microServiceName,
                limit = limit,
                offsetId = offsetId
            ).map{ toMap->
                MediaDataMinifiedImpl(
                    mediaName = toMap.mediaName,
                    mediaType = toMap.mediaType,
                    imageData = toMap.imageData,
                    requestedFromMicroService = toMap.requestedFromMicroService,
                    ownerId = toMap.ownerId,
                    id = toMap.id,
                    createdDate = toMap.createdDate
                )
            }.toCollection(ArrayList())
        return AggregatorListResponse(data = resultFromDb, databaseAccessLogId)
    }

    fun findTop1ByOrderByCreatedDateAsc(
        skipCache: Boolean = false
    ): AggregatorListResponse<FetchMostRecentMinifiedImpl> {
        val databaseAccessLogId = UUID.randomUUID().toString()
        val resultFromDb =
            mediaDataRepo.findTop1ByOrderByCreatedDateAsc()
                .map { toMap ->
                    FetchMostRecentMinifiedImpl(
                        id = toMap.id,
                        createdDate = toMap.createdDate.toInstant()
                    )
                }
                .toCollection(ArrayList())
        return AggregatorListResponse(data = ArrayList(resultFromDb), databaseAccessLogId)
    }

}