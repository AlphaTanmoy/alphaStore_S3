package com.alphaStore.alphaS3.entity

import com.alphaStore.alphaS3.entity.superentity.SuperEntityWithIdCreatedLastModifiedDataStatus
import com.fasterxml.jackson.annotation.JsonFilter
import jakarta.persistence.Column
import jakarta.persistence.Entity

@Entity(name = "jwt_black_lists")
data class JWTBlackList(
    @Column(nullable = false)
    var merchantId: String = "",
) : SuperEntityWithIdCreatedLastModifiedDataStatus()

@JsonFilter("JwtBlackListFilter")
class JWTBlackListMixIn