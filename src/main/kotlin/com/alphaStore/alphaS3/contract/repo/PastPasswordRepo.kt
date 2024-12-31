package com.alphaStore.alphaS3.contract.repo

import com.alphaStore.alphaS3.entity.PastPassword
import com.alphaStore.alphaS3.enums.DataStatus
import org.springframework.data.jpa.repository.JpaRepository

interface PastPasswordRepo : JpaRepository<PastPassword, String> {

    fun findByDataStatus(dataStatus: DataStatus = DataStatus.ACTIVE): List<PastPassword>

    fun findTop1ByOrderByCreatedDateAsc(): List<PastPassword>

    fun findTop1ByOrderByCreatedDateDesc(): List<PastPassword>

    fun findByIdAndDataStatus(id: String, dataStatus: DataStatus = DataStatus.ACTIVE): List<PastPassword>

    fun countByDataStatus(dataStatus: DataStatus = DataStatus.ACTIVE): Long

}