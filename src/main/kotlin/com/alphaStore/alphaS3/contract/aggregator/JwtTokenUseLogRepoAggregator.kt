package com.alphaStore.alphaS3.contract.aggregator

import com.alphaStore.alphaS3.contract.JwtTokenUseLogRepoAggregatorContract
import com.alphaStore.alphaS3.contract.repo.JwtTokenUseLogRepo
import com.alphaStore.alphaS3.entity.JwtTokenUseLog
import com.alphaStore.alphaS3.enums.DataStatus
import org.springframework.stereotype.Component

@Component
class JwtTokenUseLogRepoAggregator(
    private val jwtTokenUseLogRepo: JwtTokenUseLogRepo
) : JwtTokenUseLogRepoAggregatorContract {

    override fun save(entity: JwtTokenUseLog): JwtTokenUseLog {
        return jwtTokenUseLogRepo.save(entity)
    }

    override fun saveAll(entities: List<JwtTokenUseLog>) {
        jwtTokenUseLogRepo.saveAll(entities)
    }

    override fun dropTable() {
        jwtTokenUseLogRepo.dropTable()
    }

    override fun findByTokenHashAndDataStatus(
        tokenHash: String,
        dataStatus: DataStatus,
    ): List<JwtTokenUseLog> {
        return jwtTokenUseLogRepo.findByTokenHashAndDataStatus(tokenHash, dataStatus)
    }

    override fun executeFunction(queryToExecute: String): List<JwtTokenUseLog> {
        return jwtTokenUseLogRepo.executeFunction(queryToExecute)
    }
}