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
        value = "SELECT * from media_data " +
                "WHERE drive_id = :driveId"
        , nativeQuery = true
    )
    fun findByDriveId(@Param("driveId") driveId: String) : List<MediaData>

    @Query(
        value = "SELECT * " +
                "FROM media_data " +
                "ORDER BY created_date ASC " +
                "LIMIT 1",
        nativeQuery = true
    )
    fun findTop1ByOrderByCreatedDateAsc(): List<MediaData>

    @Query(
        value = "SELECT COUNT(*) FROM media_data " +
                "WHERE data_status = 'ACTIVE' " +
                "OR media_name = :mediaName " +
                "OR LOWER(media_name) LIKE LOWER(:mediaName) " +
                "OR media_type = :mediaType " +
                "OR LOWER(media_type) LIKE LOWER(:mediaType) " +
                "OR owner_id = :ownerId " +
                "OR LOWER(owner_id) LIKE LOWER(:ownerId) " +
                "OR micro_service_name = :microServiceName " +
                "OR LOWER(micro_service_name) LIKE LOWER(:microServiceName) "
        , nativeQuery = true
    )
    fun findCountWithOutOffsetDateAndLimit(
        @Param("mediaName") mediaName: String,
        @Param("mediaType") mediaType: String,
        @Param("ownerId") ownerId: String,
        @Param("microServiceName") microServiceName: String
    ): Long

    @Query(
        value = "SELECT * FROM media_data " +
                "WHERE data_status = 'ACTIVE' " +
                "OR media_name = :mediaName " +
                "OR LOWER(media_name) LIKE LOWER(:mediaName) " +
                "OR media_type = :mediaType " +
                "OR LOWER(media_type) LIKE LOWER(:mediaType) " +
                "OR owner_id = :ownerId " +
                "OR LOWER(owner_id) LIKE LOWER(:ownerId) " +
                "OR micro_service_name = :microServiceName " +
                "OR LOWER(micro_service_name) LIKE LOWER(:microServiceName) "
        , nativeQuery = true
    )
    fun findDataWithOutOffsetDateAndLimit(
        @Param("mediaName") mediaName: String,
        @Param("mediaType") mediaType: String,
        @Param("ownerId") ownerId: String,
        @Param("microServiceName") microServiceName: String
    ): List<MediaDataMinified>

    @Query(
        value = "SELECT COUNT(*) FROM media_data " +
                "WHERE created_date > :zonedDateTime " +
                "AND data_status = 'ACTIVE' " +
                "OR media_name = :mediaName " +
                "OR LOWER(media_name) LIKE LOWER(:mediaName) " +
                "OR media_type = :mediaType " +
                "OR LOWER(media_type) LIKE LOWER(:mediaType) " +
                "OR owner_id = :ownerId " +
                "OR LOWER(owner_id) LIKE LOWER(:ownerId) " +
                "OR micro_service_name = :microServiceName " +
                "OR LOWER(micro_service_name) LIKE LOWER(:microServiceName) " +
                "ORDER BY created_date ASC,id ASC " +
                "LIMIT :limit "
        , nativeQuery = true
    )
    fun findDataWithOutOffsetId(
        @Param("mediaName") mediaName: String,
        @Param("mediaType") mediaType: String,
        @Param("ownerId") ownerId: String,
        @Param("microServiceName") microServiceName: String,
        @Param("limit") limit: Int
    ): List<MediaDataMinified>

    @Query(
        value = "SELECT COUNT(*) FROM media_data " +
                "WHERE data_status = 'ACTIVE' " +
                "AND id > :offsetId " +
                "OR media_name = :mediaName " +
                "OR LOWER(media_name) LIKE LOWER(:mediaName) " +
                "OR media_type = :mediaType " +
                "OR LOWER(media_type) LIKE LOWER(:mediaType) " +
                "OR owner_id = :ownerId " +
                "OR LOWER(owner_id) LIKE LOWER(:ownerId) " +
                "OR micro_service_name = :microServiceName " +
                "OR LOWER(micro_service_name) LIKE LOWER(:microServiceName) " +
                "ORDER BY created_date ASC,id ASC " +
                "LIMIT :limit "
        , nativeQuery = true
    )
    fun findDataWithOffsetId(
        @Param("mediaName") mediaName: String,
        @Param("mediaType") mediaType: String,
        @Param("ownerId") ownerId: String,
        @Param("microServiceName") microServiceName: String,
        @Param("offsetId") offsetId: String,
        @Param("limit") limit: Int
    ): List<MediaDataMinified>

}