package com.alphaStore.alphaS3.contract

import com.alphaStore.alphaS3.entity.JWTBlackList

interface RedisObjectMasterContract {
    //////////////////////////////////////////////
    //Database result cache
    //////////////////////////////////////////////
    fun checkIfPresentDatabaseResultCache(entityName: String, resultQueryHash: String): Boolean

    fun getDatabaseResultCache(entityName: String, resultQueryHash: String): String

    fun saveDatabaseResultCacheWithEntityClass(
        entityClass: Class<*>,
        resultQueryHash: String,
        resultToSave: Any,
        selfIds: ArrayList<String>,
        timeStampString: String = System.currentTimeMillis().toString(),
    )

    fun saveDatabaseResultCacheWithKey(
        redisCacheRootKey: String,
        resultQueryHash: String,
        resultToSave: Any,
    )

    fun deleteDatabaseResultCache(entityName: String, resultQueryHash: String)

    fun deleteAllDatabaseResultCache(entityName: String)
    fun checkIfAnyHashPresentDatabaseResultCache(entityName: String): Boolean
    //////////////////////////////////////////////

    //////////////////////////////////////////////
    //Redis result cache
    //////////////////////////////////////////////
    fun checkIfPresentResultCache(entityName: String, resultQueryHash: String): Boolean

    fun getResultCache(entityName: String, resultQueryHash: String): String

    fun saveResultCache(
        entityName: String,
        resultQueryHash: String,
        resultToSave: Any,
    )

    fun deleteResultCache(entityName: String, resultQueryHash: String)

    fun deleteAllResultCache(entityName: String)
    //////////////////////////////////////////////

    //////////////////////////////////////////////
    //Table lock cache
    //////////////////////////////////////////////
    fun checkIfPresentTableLockCache(entityName: String, lockForId: String): Boolean

    fun getTableLockCache(entityName: String, lockForId: String): Boolean

    fun saveTableLockCache(entityName: String, lockForId: String, shouldLock: Boolean)

    fun deleteTableLockCache(entityName: String, lockForId: String)

    fun deleteAllTableLockCache(entityName: String)
    //////////////////////////////////////////////

    //////////////////////////////////////////////
    //user Code serial
    //////////////////////////////////////////////
    fun checkIfPresentUserCode(): Boolean

    fun getUserCode(): String

    fun saveUserCode(toSave: Int)

    fun deleteUserCode()

    fun deleteAllUserCode()
    //////////////////////////////////////////////

    //////////////////////////////////////////////
    //black listed Ips
    //////////////////////////////////////////////
    fun checkIfPresentBlockedIps(): Boolean

    fun getObjectListBlockedIps(): ArrayList<String>

    fun saveListBlockedIps(toSave: ArrayList<String>)

    fun deleteBlockedIps()
    //////////////////////////////////////////////

    //////////////////////////////////////////////
    //white listed Ips
    //////////////////////////////////////////////
    fun checkIfPresentWhiteListedIps(): Boolean

    fun getObjectListWhiteListedIps(): ArrayList<String>

    fun saveListWhiteListedIps(toSave: ArrayList<String>)

    fun deleteWhiteListedIps()
    //////////////////////////////////////////////

    //////////////////////////////////////////////
    //BlackList
    //////////////////////////////////////////////
    fun checkIfPresentJwtBlackList(userId: String): Boolean

    fun getObjectListJwtBlackList(userId: String): ArrayList<JWTBlackList>

    fun saveListJwtBlackList(toSave: ArrayList<JWTBlackList>, userId: String)

    fun deleteJwtBlackList(userId: String)

    fun getUserSessionCount(userId: String, countFromDb: Long?): Long

}