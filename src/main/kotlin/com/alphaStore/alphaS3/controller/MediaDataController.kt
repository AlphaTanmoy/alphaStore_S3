package com.alphaStore.alphaS3.controller

import com.alphaStore.alphaS3.entity.MediaData
import com.alphaStore.alphaS3.error.BadRequestException
import com.alphaStore.alphaS3.model.PaginationResponse
import com.alphaStore.alphaS3.model.UploadRequestBody
import com.alphaStore.alphaS3.model.minifiedImpl.MediaDataMinifiedImpl
import com.alphaStore.alphaS3.service.MediaDataService
import com.alphaStore.alphaS3.utils.JwtUtilMaster
import com.alphaStore.config_server.KeywordsAndConstants.HEADER_AUTHORIZATION
import com.alphaStore.alphaS3.reqres.FilterOption
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.net.URLDecoder

@RestController
@RequestMapping("/alphaS3")
class MediaDataController (
    private val mediaDataService: MediaDataService,
    private val jwtUtilMaster: JwtUtilMaster
){

    @PostMapping("/upload")
    fun uploadMediaData(
        @RequestBody imageData: MultipartFile
    ): ResponseEntity<MediaData> {

        if (imageData.isEmpty) {
            throw BadRequestException("Provide Image Data")
        }

        // Create MediaData object from the uploaded file and other details
        val mediaData = imageData.contentType?.let {
            MediaData(
                mediaName = imageData.originalFilename ?: "Unnamed",
                mediaType = imageData.contentType.toString(),
                imageData = imageData.bytes,
                requestedFromMicroService = "requestedFromMicroService",
                purpose = "Upload"  // You can adjust the purpose as needed
            )
        }

        // Save the media data
        val savedMediaData = mediaData?.let { mediaDataService.saveMediaData(it) }

        // Return the saved media data in the response
        return if (savedMediaData != null) {
            ResponseEntity.ok(savedMediaData)
        } else {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
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