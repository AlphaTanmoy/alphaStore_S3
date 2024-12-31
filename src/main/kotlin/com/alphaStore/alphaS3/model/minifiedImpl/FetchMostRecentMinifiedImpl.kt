package com.alphaStore.alphaS3.model.minifiedImpl

import com.alphaStore.alphaS3.model.minified.FetchMostRecentMinified
import java.time.Instant

data class FetchMostRecentMinifiedImpl(
    override var id: String,
    override var createdDate: Instant
) : FetchMostRecentMinified