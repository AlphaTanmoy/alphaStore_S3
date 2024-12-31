package com.alphaStore.alphaS3.model.minified

import java.time.Instant

interface FetchMostRecentMinified {
    var id: String
    var createdDate: Instant
}