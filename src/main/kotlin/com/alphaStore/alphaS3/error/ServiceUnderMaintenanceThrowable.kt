package com.alphaStore.alphaS3.error

class ServiceUnderMaintenanceThrowable(
    var errorMessage: String = "",
) : RuntimeException()