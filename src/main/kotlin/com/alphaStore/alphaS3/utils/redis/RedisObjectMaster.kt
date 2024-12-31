package com.alphaStore.alphaS3.utils.redis


import com.alphaStore.alphaS3.contract.RedisObjectMasterContract
import com.alphaStore.alphaS3.entity.JWTBlackList
import com.alphaStore.alphaS3.enums.AccessRole
import com.alphaStore.alphaS3.utils.ConverterStringToObjectList
import org.slf4j.Logger
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.slf4j.LoggerFactory

@Component
@Primary
class RedisObjectMaster(
    private val redisTemplate: RedisTemplate<String, String>,
    private val redisKeyUtil: RedisKeyUtil,
    private val redisCredentials: RedisCredentials
) : RedisObjectMasterContract {

    private val redisUtil = RedisUtil()
    private lateinit var redisHashOperation: HashOperations<String, String, String>
    private val logger: Logger = LoggerFactory.getLogger(RedisObjectMaster::class.java)

    private fun getRedisHasOperation(): HashOperations<String, String, String> {
        if (!this::redisHashOperation.isInitialized)
            redisHashOperation = redisTemplate.opsForHash<String, String>()
        return redisHashOperation
    }

    //////////////////////////////////////////////
    //All
    //////////////////////////////////////////////
    fun deleteAll() {
        getRedisHasOperation().keys(redisKeyUtil.getKey(redisCredentials.REDIS_KEY_JWT_BLACK_LIST))
            .iterator().forEach { item ->
                getRedisHasOperation().delete(redisKeyUtil.getKey(redisCredentials.REDIS_KEY_JWT_BLACK_LIST), item)
            }
        getRedisHasOperation().keys(redisKeyUtil.getKey(redisCredentials.REDIS_KEY_BLACKLISTED_IPS))
            .iterator().forEach { item ->
                getRedisHasOperation().delete(redisKeyUtil.getKey(redisCredentials.REDIS_KEY_BLACKLISTED_IPS), item)
            }
        getRedisHasOperation().keys(redisKeyUtil.getKey(redisCredentials.REDIS_KEY_WHITELISTED_IPS))
            .iterator().forEach { item ->
                getRedisHasOperation().delete(redisKeyUtil.getKey(redisCredentials.REDIS_KEY_WHITELISTED_IPS), item)
            }
    }

    fun flushAll() {
        // redisTemplate.connectionFactory?.connection?.flushAll()
    }
    //////////////////////////////////////////////

    //////////////////////////////////////////////
    //BlackList
    //////////////////////////////////////////////
    override fun checkIfPresentJwtBlackList(userId: String): Boolean {
        return getRedisHasOperation().hasKey(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_JWT_BLACK_LIST),
            userId
        )
    }

    override fun getObjectListJwtBlackList(userId: String): ArrayList<JWTBlackList> {
        return redisUtil.getObjectListFromRedis(
            redisHashOperation,
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_JWT_BLACK_LIST),
            userId
        )
    }

    override fun saveListJwtBlackList(toSave: ArrayList<JWTBlackList>, userId: String) {
        redisUtil.saveToRedis(
            getRedisHasOperation(),
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_JWT_BLACK_LIST),
            userId,
            ConverterStringToObjectList.convertObjectToString(toSave)
        )
    }

    override fun deleteJwtBlackList(userId: String) {
        getRedisHasOperation().delete(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_JWT_BLACK_LIST),
            userId
        )
    }
    //////////////////////////////////////////////

    //////////////////////////////////////////////
    //black listed Ips
    //////////////////////////////////////////////
    override fun checkIfPresentBlockedIps(): Boolean {
        return getRedisHasOperation().hasKey(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_BLACKLISTED_IPS),
            "all"
        )
    }

    override fun getObjectListBlockedIps(): ArrayList<String> {
        return redisUtil.getObjectListFromRedis(
            redisHashOperation,
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_BLACKLISTED_IPS),
            "all"
        )
    }

    override fun saveListBlockedIps(toSave: ArrayList<String>) {
        redisUtil.saveToRedis(
            getRedisHasOperation(),
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_BLACKLISTED_IPS),
            "all",
            ConverterStringToObjectList.convertObjectToString(toSave)
        )
    }

    override fun deleteBlockedIps() {
        getRedisHasOperation().delete(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_BLACKLISTED_IPS),
            "all"
        )
    }
    //////////////////////////////////////////////

    //////////////////////////////////////////////
    //white listed Ips
    //////////////////////////////////////////////
    override fun checkIfPresentWhiteListedIps(): Boolean {
        return getRedisHasOperation().hasKey(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_WHITELISTED_IPS),
            "all"
        )
    }

    override fun getObjectListWhiteListedIps(): ArrayList<String> {
        return redisUtil.getObjectListFromRedis(
            redisHashOperation,
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_WHITELISTED_IPS),
            "all"
        )
    }

    override fun saveListWhiteListedIps(toSave: ArrayList<String>) {
        redisUtil.saveToRedis(
            getRedisHasOperation(),
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_WHITELISTED_IPS),
            "all",
            ConverterStringToObjectList.convertObjectToString(toSave)
        )
    }

    override fun deleteWhiteListedIps() {
        getRedisHasOperation().delete(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_WHITELISTED_IPS),
            "all"
        )
    }
    //////////////////////////////////////////////

    //////////////////////////////////////////////
    //Access Roles
    //////////////////////////////////////////////
    fun checkIfPresentAccessRole(hash: String): Boolean {
        return getRedisHasOperation().hasKey(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_ACCESS_ROLE),
            hash
        )
    }

    fun getObjectAccessRole(hash: String): AccessRole {
        return redisUtil.getObjectFromRedis(
            redisHashOperation,
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_ACCESS_ROLE),
            hash
        )
    }

    fun saveAccessRole(toSave: AccessRole, hash: String) {
        redisUtil.saveToRedis(
            getRedisHasOperation(),
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_ACCESS_ROLE),
            hash,
            ConverterStringToObjectList.convertObjectToString(toSave)
        )
    }

    fun deleteAccessRole(hash: String) {
        getRedisHasOperation().delete(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_ACCESS_ROLE),
            hash
        )
    }

    fun deleteAllAccessRole() {
        getRedisHasOperation().keys(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_ACCESS_ROLE),
        ).forEach { key ->
            getRedisHasOperation().delete(
                redisKeyUtil.getKey(redisCredentials.REDIS_KEY_ACCESS_ROLE),
                key
            )
        }
    }
    //////////////////////////////////////////////

    //////////////////////////////////////////////
    //user Code serial
    //////////////////////////////////////////////
    override fun checkIfPresentUserCode(): Boolean {
        return getRedisHasOperation().hasKey(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_USER_SERIAL),
            "serial"
        )
    }

    override fun getUserCode(): String {
        return redisUtil.getStringFromRedis(
            redisHashOperation,
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_USER_SERIAL),
            "serial"
        )
    }

    override fun saveUserCode(toSave: Int) {
        redisUtil.saveToRedis(
            getRedisHasOperation(),
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_USER_SERIAL),
            "serial",
            toSave.toString()
        )
    }

    override fun deleteUserCode() {
        getRedisHasOperation().delete(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_USER_SERIAL),
            "serial"
        )
    }

    override fun deleteAllUserCode() {
        getRedisHasOperation().keys(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_USER_SERIAL),
        ).forEach { key ->
            getRedisHasOperation().delete(
                redisKeyUtil.getKey(redisCredentials.REDIS_KEY_USER_SERIAL),
                key
            )
        }
    }

    //////////////////////////////////////////////

    //////////////////////////////////////////////
    //Application Code serial
    //////////////////////////////////////////////

    override fun getUserSessionCount(userId: String, countFromDb: Long?): Long {
        val accessCountKey = "user:$userId:accessCount"
        val currentCount = redisTemplate.opsForValue().get(accessCountKey)?.toLong()
            ?: countFromDb?.also {
                redisTemplate.opsForValue().set(accessCountKey, it.toString())
            } ?: 0L
        return redisTemplate.opsForValue().increment(accessCountKey, 1) ?: (currentCount + 1)
    }


    //////////////////////////////////////////////

    //////////////////////////////////////////////
    //Access control name with path
    //////////////////////////////////////////////
    fun checkIfPresentAccessControlPath(accessControlName: String): Boolean {
        return getRedisHasOperation().hasKey(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_ACCESS_CONTROL),
            accessControlName
        )
    }

    fun getAccessControlPath(accessControlName: String): String {
        return redisUtil.getStringFromRedis(
            redisHashOperation,
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_ACCESS_CONTROL),
            accessControlName
        )
    }

    fun saveAccessControlPath(accessControlName: String, path: String) {
        redisUtil.saveToRedis(
            getRedisHasOperation(),
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_ACCESS_CONTROL),
            accessControlName,
            path
        )
    }

    fun deleteAccessControlPath(accessControlName: String) {
        getRedisHasOperation().delete(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_ACCESS_CONTROL),
            accessControlName
        )
    }

    fun deleteAllAccessControlPath() {
        getRedisHasOperation().keys(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_ACCESS_CONTROL),
        ).forEach { key ->
            getRedisHasOperation().delete(
                redisKeyUtil.getKey(redisCredentials.REDIS_KEY_ACCESS_CONTROL),
                key
            )
        }
    }
    //////////////////////////////////////////////

    //////////////////////////////////////////////
    //Database result cache
    //////////////////////////////////////////////
    override fun checkIfPresentDatabaseResultCache(entityName: String, resultQueryHash: String): Boolean {
        return getRedisHasOperation().hasKey(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_DATABASE_RESULT) + entityName,
            resultQueryHash
        )
    }

    override fun getDatabaseResultCache(entityName: String, resultQueryHash: String): String {
        /*entityAndTableNameMaster.getAllEntityNames().data.find { toFind -> toFind == entityName }?.let {
            rabbitTemplate.convertAndSend(
                rabbitMqUtils.getKeyFor(RABBIT_MQ_EXCHANGE),
                rabbitMqUtils.getKeyFor(RABBIT_MQ_ROUTE_KEY_FOR_CHECKING_TOUCHED_DATA),
                SearchedHashDataRabbitMqRequest(
                    entityName,
                    resultQueryHash
                )
            )
        }*/
        return redisUtil.getStringFromRedis(
            redisHashOperation,
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_DATABASE_RESULT) + entityName,
            resultQueryHash
        )
    }

    @Async
    override fun saveDatabaseResultCacheWithEntityClass(
        entityClass: Class<*>,
        resultQueryHash: String,
        resultToSave: Any,
        selfIds: ArrayList<String>,
        timeStampString: String,
    ) {
        try {
            redisUtil.saveToRedis(
                getRedisHasOperation(),
                redisKeyUtil.getKey(redisCredentials.REDIS_KEY_DATABASE_RESULT) + entityClass.simpleName,
                resultQueryHash,
                ConverterStringToObjectList.convertObjectToString(
                    resultToSave,
                )
            )
        } catch (ex: Exception) {
            logger.error("Failed to save DB result to cache.")
        }
    }

    override fun saveDatabaseResultCacheWithKey(
        redisCacheRootKey: String,
        resultQueryHash: String,
        resultToSave: Any,
    ) {
        redisUtil.saveToRedis(
            getRedisHasOperation(),
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_DATABASE_RESULT) + redisCacheRootKey,
            resultQueryHash,
            ConverterStringToObjectList.convertObjectToString(
                resultToSave,
            )
        )
    }

    override fun deleteDatabaseResultCache(entityName: String, resultQueryHash: String) {
        getRedisHasOperation().delete(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_DATABASE_RESULT) + entityName,
            resultQueryHash
        )
    }

    override fun checkIfAnyHashPresentDatabaseResultCache(entityName: String): Boolean {
        return getRedisHasOperation().size(redisKeyUtil.getKey(redisCredentials.REDIS_KEY_DATABASE_RESULT) + entityName) != 0.toLong()
    }

    override fun deleteAllDatabaseResultCache(entityName: String) {
        getRedisHasOperation().keys(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_DATABASE_RESULT) + entityName,
        ).forEach { key ->
            getRedisHasOperation().delete(
                redisKeyUtil.getKey(redisCredentials.REDIS_KEY_DATABASE_RESULT) + entityName,
                key
            )
        }
    }

    //////////////////////////////////////////////

    //////////////////////////////////////////////
    //Redis result cache
    //////////////////////////////////////////////
    override fun checkIfPresentResultCache(entityName: String, resultQueryHash: String): Boolean {
        return getRedisHasOperation().hasKey(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_RESULT) + entityName,
            resultQueryHash
        )
    }

    override fun getResultCache(entityName: String, resultQueryHash: String): String {
        return redisUtil.getStringFromRedis(
            redisHashOperation,
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_RESULT) + entityName,
            resultQueryHash
        )
    }

    override fun saveResultCache(
        entityName: String,
        resultQueryHash: String,
        resultToSave: Any,
    ) {
        redisUtil.saveToRedis(
            getRedisHasOperation(),
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_RESULT) + entityName,
            resultQueryHash,
            ConverterStringToObjectList.convertObjectToString(
                resultToSave,
            )
        )
    }

    override fun deleteResultCache(entityName: String, resultQueryHash: String) {
        getRedisHasOperation().delete(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_RESULT) + entityName,
            resultQueryHash
        )
    }

    override fun deleteAllResultCache(entityName: String) {
        getRedisHasOperation().keys(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_RESULT) + entityName,
        ).forEach { key ->
            getRedisHasOperation().delete(
                redisKeyUtil.getKey(redisCredentials.REDIS_KEY_RESULT) + entityName,
                key
            )
        }
    }
    //////////////////////////////////////////////

    //////////////////////////////////////////////
    //Table lock cache
    //////////////////////////////////////////////
    override fun checkIfPresentTableLockCache(entityName: String, lockForId: String): Boolean {
        return getRedisHasOperation().hasKey(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_DATABASE_TABLE_LOCK) + entityName,
            lockForId
        )
    }

    override fun getTableLockCache(entityName: String, lockForId: String): Boolean {
        val result = redisUtil.getStringFromRedis(
            redisHashOperation,
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_DATABASE_TABLE_LOCK) + entityName,
            lockForId
        )
        return result.toBoolean()
    }

    override fun saveTableLockCache(entityName: String, lockForId: String, shouldLock: Boolean) {
        redisUtil.saveToRedis(
            getRedisHasOperation(),
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_DATABASE_TABLE_LOCK) + entityName,
            lockForId,
            shouldLock.toString()
        )
    }

    override fun deleteTableLockCache(entityName: String, lockForId: String) {
        getRedisHasOperation().delete(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_DATABASE_TABLE_LOCK) + entityName,
            lockForId
        )
    }

    override fun deleteAllTableLockCache(entityName: String) {
        getRedisHasOperation().keys(
            redisKeyUtil.getKey(redisCredentials.REDIS_KEY_DATABASE_TABLE_LOCK) + entityName,
        ).forEach { key ->
            getRedisHasOperation().delete(
                redisKeyUtil.getKey(redisCredentials.REDIS_KEY_DATABASE_TABLE_LOCK),
                key
            )
        }
    }
}