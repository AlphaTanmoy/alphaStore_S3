package com.alphaStore.alphaS3.contract

import com.alphaStore.alphaS3.entity.JwtTokenUseLog
import com.alphaStore.alphaS3.enums.DataStatus

interface JwtTokenUseLogRepoAggregatorContract {

    fun save(entity: JwtTokenUseLog): JwtTokenUseLog

    fun saveAll(entities: List<JwtTokenUseLog>)

    fun dropTable()

    fun findByTokenHashAndDataStatus(
        tokenHash: String,
        dataStatus: DataStatus = DataStatus.ACTIVE,
    ): List<JwtTokenUseLog>

    fun executeFunction(queryToExecute: String): List<JwtTokenUseLog>
}