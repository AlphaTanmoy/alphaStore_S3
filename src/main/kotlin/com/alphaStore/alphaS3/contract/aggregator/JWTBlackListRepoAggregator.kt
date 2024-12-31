package com.alphaStore.alphaS3.contract.aggregator

import com.alphaStore.alphaS3.contract.repo.JWTBlackListRepo
import com.alphaStore.alphaS3.entity.JWTBlackList
import com.alphaStore.alphaS3.enums.DataStatus
import org.springframework.stereotype.Component

@Component
class JWTBlackListRepoAggregator(
    private val jwtBlackListRepo: JWTBlackListRepo
) {

    fun save(entity: JWTBlackList): JWTBlackList {
        return jwtBlackListRepo.save(entity)
    }

    fun saveAll(entities: List<JWTBlackList>) {
        jwtBlackListRepo.saveAll(entities)
    }

    fun findByMerchantIdAndDataStatus(
        merchantId: String,
        dataStatus: DataStatus = DataStatus.ACTIVE,
        forceMaster: Boolean = false
    ): List<JWTBlackList> {
        return jwtBlackListRepo.findByMerchantIdAndDataStatus(merchantId, dataStatus)
    }
}