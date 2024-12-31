package com.alphaStore.alphaS3.reqres

import java.util.*

data class AggregatorListResponse<T>(
    var data: ArrayList<T> = arrayListOf(),
    var databaseAccessLogId: String = UUID.randomUUID().toString()
)
