package com.alphaStore.alphaS3.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ChangePasswordRequest(
    @field:NotNull(message = "Please provide current password")
    @field:NotBlank(message = "Please provide valid current password")
    var currentPassword: String = "",
    @field:NotNull(message = "Please provide  new password")
    @field:NotBlank(message = "Please provide valid new password")
    var newPassword: String = "",
)
