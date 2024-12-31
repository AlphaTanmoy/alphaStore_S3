package com.alphaStore.alphaS3.contract

interface RedisCacheCleanerMasterContract {
    fun clean(
        changedEntityName: String,
        processedList: ArrayList<String> = ArrayList(),
        idsChanged: ArrayList<String>? = null
    )
}