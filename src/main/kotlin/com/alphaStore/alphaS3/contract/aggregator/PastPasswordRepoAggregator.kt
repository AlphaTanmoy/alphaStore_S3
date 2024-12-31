package com.alphaStore.alphaS3.contract.aggregator

import com.alphaStore.alphaS3.contract.repo.PastPasswordRepo
import com.alphaStore.alphaS3.entity.PastPassword
import org.springframework.stereotype.Component

@Component
class PastPasswordRepoAggregator(
    private val pastPasswordRepo: PastPasswordRepo
) {
    fun save(entity: PastPassword): PastPassword {
        return pastPasswordRepo.save(entity)
    }

    fun saveAll(entities: List<PastPassword>) {
        pastPasswordRepo.saveAll(entities)
    }

}