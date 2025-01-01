package com.alphaStore.alphaS3.contract.repo

import com.alphaStore.alphaS3.entity.MediaData
import com.alphaStore.alphaS3.model.minified.MediaDataMinified
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.ZonedDateTime


interface MediaDataRepo : JpaRepository<MediaData, String> {


    @Query(
        value = "SELECT * from media_data " +
                "WHERE media_name = :name"
        , nativeQuery = true
    )
    fun findByName(@Param("name") name: String): List<MediaDataMinified>

    @Query(
        value = "SELECT * " +
                "FROM media_data " +
                "ORDER BY created_date ASC " +
                "LIMIT 1",
        nativeQuery = true
    )
    fun findTop1ByOrderByCreatedDateAsc(): List<MediaData>

    @Query(
        value = ""
        , nativeQuery = true
    )
    fun findCountWithOutOffsetIdAndDate(
        @Param("mediaName") mediaName: String,
        @Param("mediaType") mediaType: String,
        @Param("ownerId") ownerId: String,
        @Param("microServiceName") microServiceName: String
    ): Long

    @Query(
        value = ""
        , nativeQuery = true
    )
    fun findDataWithOutOffsetIdAndDate(
        @Param("mediaName") mediaName: String,
        @Param("mediaType") mediaType: String,
        @Param("ownerId") ownerId: String,
        @Param("microServiceName") microServiceName: String
    ): List<MediaDataMinified>

    @Query(
        value = ""
        , nativeQuery = true
    )
    fun findDataWithOutOffsetId(
        @Param("mediaName") mediaName: String,
        @Param("mediaType") mediaType: String,
        @Param("ownerId") ownerId: String,
        @Param("microServiceName") microServiceName: String,
        @Param("offsetDate") offsetDate: ZonedDateTime,
        @Param("limit") limit: Int
    ): List<MediaDataMinified>

    @Query(
        value = ""
        , nativeQuery = true
    )
    fun findDataWithOffsetId(
        @Param("mediaName") mediaName: String,
        @Param("mediaType") mediaType: String,
        @Param("ownerId") ownerId: String,
        @Param("microServiceName") microServiceName: String,
        @Param("offsetDate") offsetDate: ZonedDateTime,
        @Param("offsetId") offsetId: String,
        @Param("limit") limit: Int
    ): List<MediaDataMinified>

}