package com.alphaStore.alphaS3.utils.redis

import com.alphaStore.alphaS3.contract.RedisObjectMasterContract
import com.alphaStore.alphaS3.contract.RedisCacheCleanerMasterContract

class RedisCacheCleanerMaster (
    private val redisObjectMasterContract: RedisObjectMasterContract
): RedisCacheCleanerMasterContract {

    override fun clean(changedEntityName: String, processedList: ArrayList<String>, idsChanged: ArrayList<String>?) {
        processedList.find { toFind -> toFind == changedEntityName }?.let { return }
        processedList.add(changedEntityName)
        redisObjectMasterContract.deleteAllDatabaseResultCache(changedEntityName)
        hierarchy().filter { toFilter ->
            toFilter.containsDataFromEntity.find { toFind ->
                toFind.entityName == changedEntityName
            }?.let { _ ->
                true
            } ?: run {
                false
            }
        }.let { parentsContainingChangedEntityData ->
            parentsContainingChangedEntityData.forEach { parentContainingChangedEntityData ->
                clean(parentContainingChangedEntityData.entityName, processedList)
            }
        }
    }

    private fun hierarchy(): ArrayList<DatabaseEntityHierarchy> {
        return arrayListOf()
    }
}

data class DatabaseEntityHierarchy(
    var entityName: String = "",
    var containsDataFromEntity: ArrayList<DatabaseEntityHierarchy> = ArrayList(),
)