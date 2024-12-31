package com.alphaStore.alphaS3.model

data class IpDataFinderResponse(
    var countryName: String = "",
    var lat: String = "",
    var lng: String = "",
    var data: String = "",
    var ip: String = ""
)