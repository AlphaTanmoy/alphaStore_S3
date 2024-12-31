package com.alphaStore.alphaS3.entity

import com.alphaStore.alphaS3.entity.superentity.SuperEntityWithIdCreatedLastModifiedDataStatus
import com.fasterxml.jackson.annotation.JsonFilter
import jakarta.persistence.Column
import jakarta.persistence.Entity

@Entity(name = "past_passwords")
data class PastPassword(
    @Column(nullable = false)
    var passwordHash: String = "",
) : SuperEntityWithIdCreatedLastModifiedDataStatus()

@JsonFilter("PastPasswordFilter")
class PastPasswordMixIn