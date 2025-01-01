package com.alphaStore.alphaS3.controller

import com.alphaStore.alphaS3.entity.MediaData
import com.alphaStore.alphaS3.error.BadRequestException
import com.alphaStore.alphaS3.model.PaginationResponse
import com.alphaStore.alphaS3.model.minifiedImpl.MediaDataMinifiedImpl
import com.alphaStore.alphaS3.service.MediaDataService
import com.alphaStore.alphaS3.utils.JwtUtilMaster
import com.alphaStore.config_server.KeywordsAndConstants.HEADER_AUTHORIZATION
import com.alphaStore.alphaS3.reqres.FilterOption
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.net.URLDecoder

@RestController
@RequestMapping("/images")
class MediaDataController (
    private val mediaDataService: MediaDataService,
    private val jwtUtilMaster: JwtUtilMaster
){

    @PostMapping("/upload")
    fun uploadMediaData(
        @RequestParam("mediaName") mediaName: String,
        @RequestParam("mediaType") mediaType: String,
        @RequestParam("imageData") imageData: MultipartFile,
        @RequestParam("requestedFromMicroService") requestedFromMicroService: String,
        @RequestParam(value = "purpose", required = false) purpose: String?
    ): ResponseEntity<MediaData> {

        val finalPurpose = if(purpose.isNullOrEmpty()) "upload" else purpose

        val mediaData = MediaData(
            mediaName = mediaName,
            mediaType = mediaType,
            imageData = imageData.bytes,
            requestedFromMicroService = requestedFromMicroService,
            purpose = finalPurpose
        )

        // Save the MediaData object in the database
        val savedMediaData = mediaDataService.saveMediaData(mediaData)

        // Return the saved MediaData object as a response
        return ResponseEntity.ok(savedMediaData)
    }


    @GetMapping("/getAll")
    fun getAllImages(
        @Valid
        @RequestHeader(HEADER_AUTHORIZATION) token: String,
        @RequestParam("mediaName") mediaName: String? = null,
        @RequestParam("mediaType") mediaType: String? = null,
        @RequestParam("ownerId") ownerId: String? = null,
        @RequestParam("microServiceName") microServiceName: String? = null,
        @RequestParam("offsetToken") offsetToken: String? = null,
        @RequestParam("giveCount") giveCount: Boolean = false,
        @RequestParam("considerMaxDateRange", defaultValue = "false") considerMaxDateRange: Boolean = false,
        @RequestParam("limit") limit: Int? = null,
        @RequestParam("giveData", defaultValue = "true") giveData: Boolean = true,
    ): PaginationResponse<MediaDataMinifiedImpl> {
        val toRetFilterOption: ArrayList<FilterOption> = ArrayList()

        val userType = jwtUtilMaster.getUserType(token)
        if (userType.isEmpty || userType.get().nameDescriptor != "Admin") {
            throw BadRequestException(errorMessage = "You are not admin!")
        }

        var mediaNameFinal = "%"
        mediaName?.let { obj ->
            toRetFilterOption.add(FilterOption("mediaName", obj, obj))
            mediaNameFinal = obj.split(',').joinToString("|") { "%${URLDecoder.decode(it, "UTF-8")}%" }
        }

        var mediaTypeFinal = "%"
        mediaType?.let { obj ->
            toRetFilterOption.add(FilterOption("mediaType", obj, obj))
            mediaTypeFinal = obj.split(',').joinToString("|") { "%${URLDecoder.decode(it, "UTF-8")}%" }
        }

        var ownerIdFinal = "%"
        ownerId?.let { obj ->
            toRetFilterOption.add(FilterOption("ownerId", obj, obj))
            ownerIdFinal = obj.split(',').joinToString("|") { "%${URLDecoder.decode(it, "UTF-8")}%" }
        }

        var microServiceNameFinal = "%"
        microServiceName?.let { obj ->
            toRetFilterOption.add(FilterOption("microServiceName", obj, obj))
            microServiceNameFinal = obj.split(',').joinToString("|") { "%${URLDecoder.decode(it, "UTF-8")}%" }
        }

        var pageSizeFinal = 5
        limit?.let {
            pageSizeFinal =
                if (it > 20)
                    20
                else
                    it
        }

        val resultFromService = mediaDataService.getAllImages(
            mediaName = mediaNameFinal,
            mediaType = mediaTypeFinal,
            ownerId = ownerIdFinal,
            microServiceName = microServiceNameFinal,
            offsetToken = offsetToken,
            considerMaxDateRange = considerMaxDateRange,
            giveCount = giveCount,
            limit = pageSizeFinal,
            giveData = giveData,
            toRetFilterOption = toRetFilterOption
        )

        return resultFromService
    }

}