package com.alphaStore.alphaS3.entity

import com.alphaStore.alphaS3.entity.superentity.SuperEntityWithIdCreatedLastModifiedDataStatus
import com.fasterxml.jackson.annotation.JsonFilter
import jakarta.persistence.Column
import jakarta.persistence.Entity
import java.time.ZonedDateTime

@Entity(name = "jwt_token_use_logs")
data class JwtTokenUseLog(
    @Column(nullable = false)
    var tokenHash: String = "",
    @Column(nullable = false)
    var expiry: ZonedDateTime = ZonedDateTime.now(),
) : SuperEntityWithIdCreatedLastModifiedDataStatus()

@JsonFilter("JwtTokenUseLogFilter")
class JwtTokenUseLogMixIn