package com.alphaStore.alphaS3.reqres

import java.util.*

data class AggregatorResponse<T>(
    var data: T,
    var databaseAccessLogId: String = UUID.randomUUID().toString()
)
