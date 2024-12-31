package com.alphaStore.alphaS3.contract.repo

import com.alphaStore.alphaS3.entity.JWTBlackList
import com.alphaStore.alphaS3.enums.DataStatus
import org.springframework.data.jpa.repository.JpaRepository

interface JWTBlackListRepo : JpaRepository<JWTBlackList, String> {

    fun findByMerchantIdAndDataStatus(
        merchantId: String,
        dataStatus: DataStatus = DataStatus.ACTIVE
    ): List<JWTBlackList>

    fun findByDataStatus(dataStatus: DataStatus = DataStatus.ACTIVE): List<JWTBlackList>

    fun findTop1ByOrderByCreatedDateAsc(): List<JWTBlackList>
    fun findTop1ByOrderByCreatedDateDesc(): List<JWTBlackList>
    fun countByDataStatus(dataStatus: DataStatus = DataStatus.ACTIVE): Long

}