package com.alphaStore.alphaS3.contract.repo

import com.alphaStore.alphaS3.entity.JwtTokenUseLog
import com.alphaStore.alphaS3.enums.DataStatus
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@Suppress("SqlDialectInspection", "SqlNoDataSourceInspection")
interface JwtTokenUseLogRepo : JpaRepository<JwtTokenUseLog, String> {
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(
        value = "DROP TABLE jwt_token_use_logs CASCADE",
        nativeQuery = true
    )
    fun dropTable()

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(
        value = "CREATE OR REPLACE FUNCTION execute_jwt_token_use_logs_queries (query_to_execute IN VARCHAR) RETURNS SETOF jwt_token_use_logs " +
                "AS " +
                "\$BODY\$ " +
                "DECLARE " +
                "BEGIN " +
                "RETURN QUERY EXECUTE query_to_execute; " +
                "END; " +
                "\$BODY\$ " +
                "LANGUAGE PLPGSQL;",
        nativeQuery = true
    )
    fun createQueryExecutionStoredFunction()

    @Query(
        value = "SELECT * FROM execute_jwt_token_use_logs_queries(:queryToExecute)",
        nativeQuery = true
    )
    fun executeFunction(@Param("queryToExecute") queryToExecute: String): List<JwtTokenUseLog>
    fun findByTokenHashAndDataStatus(
        tokenHash: String,
        dataStatus: DataStatus = DataStatus.ACTIVE
    ): List<JwtTokenUseLog>

    fun findByDataStatus(dataStatus: DataStatus = DataStatus.ACTIVE): List<JwtTokenUseLog>

    fun findTop1ByOrderByCreatedDateAsc(): List<JwtTokenUseLog>
    fun findTop1ByOrderByCreatedDateDesc(): List<JwtTokenUseLog>
    fun countByDataStatus(dataStatus: DataStatus = DataStatus.ACTIVE): Long
}