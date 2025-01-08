package com.alphaStore.alphaS3.controller

import com.alphaStore.alphaS3.entity.MediaData
import com.alphaStore.alphaS3.enums.MediaExtentions
import com.alphaStore.alphaS3.enums.MediaType
import com.alphaStore.alphaS3.error.BadRequestException
import com.alphaStore.alphaS3.model.PaginationResponse
import com.alphaStore.alphaS3.model.minifiedImpl.MediaDataMinifiedImpl
import com.alphaStore.alphaS3.service.MediaDataService
import com.alphaStore.alphaS3.utils.JwtUtilMaster
import com.alphaStore.config_server.KeywordsAndConstants.HEADER_AUTHORIZATION
import com.alphaStore.alphaS3.reqres.FilterOption
import com.alphaStore.alphaS3.reqres.ReturnList
import com.alphaStore.alphaS3.reqres.TempList
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.URLDecoder

@RestController
@RequestMapping("/alphaS3")
class MediaDataController (
    private val mediaDataService: MediaDataService,
    private val jwtUtilMaster: JwtUtilMaster
){

    @PostMapping("/test")
    fun uploadTest(
        @RequestBody addImage: MultipartFile
    ){
        val uploadDirectory = "src/main/resources/static/"
        var adsImagesString = ""


        adsImagesString += mediaDataService.saveImageToStorage(uploadDirectory, addImage) + ","

    }

    /*@GetMapping("/getImages/{adsId}")
    @Throws(IOException::class)
    fun getImages(@PathVariable adsId: Long): List<ByteArray> {
        val imageDirectory = "src/main/resources/static/images/ads"

        val imageNames = advertiserService.getAdsImages(adsId).split(",")
        val imageBytesList = mutableListOf<ByteArray>()

        for (imageName in imageNames) {
            val imageBytes = mediaDataService.getImage(imageDirectory, imageName)
            imageBytes?.let { imageBytesList.add(it) }
        }

        return imageBytesList
    }*/


    @PostMapping("/upload")
    fun uploadMediaData(
        @RequestBody getImageRequest: TempList
    ): ArrayList<ReturnList> {

        val returnData : ArrayList<ReturnList> = arrayListOf()

        getImageRequest.items.forEach{
            if (it.imageLink.isEmpty()) {
                throw BadRequestException("Provide Image Link")
            }

            val getFileId = mediaDataService.extractFileId(it.imageLink);

            val imageData = MediaData(
                actualName = it.name,
                mediaExtentions = MediaExtentions.PNG,
                mediaType = MediaType.IMAGE,
                imageData = it.imageLink,
                requestedFromMicroService = it.microServiceName,
                driveId = getFileId!!
            )

            mediaDataService.saveMediaData(imageData)

            val id = mediaDataService.findByDriveId(getFileId)
            print("ID: $id")

            returnData.add(
                ReturnList(
                    name = it.name,
                    data = it.imageLink
                )
            )

        }
        return returnData
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