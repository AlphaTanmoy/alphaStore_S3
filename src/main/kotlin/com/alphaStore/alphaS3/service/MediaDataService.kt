package com.alphaStore.alphaS3.service

import com.alphaStore.alphaS3.model.PaginationResponse
import com.alphaStore.alphaS3.model.minifiedImpl.MediaDataMinifiedImpl
import com.alphaStore.alphaS3.utils.DateUtil
import com.alphaStore.alphaS3.contract.EncodingUtilContract
import com.alphaStore.alphaS3.contract.EncryptionMasterContract
import com.alphaStore.alphaS3.contract.aggregator.MediaDataAggregator
import com.alphaStore.alphaS3.entity.MediaData
import com.alphaStore.alphaS3.enums.DateRangeType
import com.alphaStore.alphaS3.error.BadRequestException
import com.alphaStore.alphaS3.reqres.FilterOption
import com.alphaStore.alphaS3.utils.ConverterStringToObjectList
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.time.ZoneId
import java.time.ZonedDateTime
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.UUID

@Component
class MediaDataService (
    private val mediaDataAggregator: MediaDataAggregator,
    private val encodingUtilContract: EncodingUtilContract,
    private val encryptionMaster: EncryptionMasterContract,
    private val dateUtilContract: DateUtil,
){

    @Throws(IOException::class)
    fun saveImageToStorage(uploadDirectory: String, imageFile: MultipartFile): String {
        val uniqueFileName = "${UUID.randomUUID()}_${imageFile.originalFilename}"

        val uploadPath = Path.of(uploadDirectory)
        val filePath = uploadPath.resolve(uniqueFileName)

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath)
        }

        imageFile.inputStream.use { inputStream ->
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)
        }

        return uniqueFileName
    }

    // To view an image
    @Throws(IOException::class)
    fun getImage(imageDirectory: String, imageName: String): ByteArray? {
        val imagePath = Path.of(imageDirectory, imageName)

        return if (Files.exists(imagePath)) {
            Files.readAllBytes(imagePath)
        } else {
            null // Handle missing images
        }
    }

    // Delete an image
    @Throws(IOException::class)
    fun deleteImage(imageDirectory: String, imageName: String): String {
        val imagePath = Path.of(imageDirectory, imageName)

        return if (Files.exists(imagePath)) {
            Files.delete(imagePath)
            "Success"
        } else {
            "Failed" // Handle missing images
        }
    }


    fun saveMediaData(mediaData: MediaData): MediaData {
        return mediaDataAggregator.save(mediaData)
    }

    fun extractFileId(url: String): String? {
        val regex = Regex("/d/([a-zA-Z0-9_-]+)")
        val matchResult = regex.find(url)
        return matchResult?.groups?.get(1)?.value
    }


    fun findByDriveId(driveId: String) : String{
        return mediaDataAggregator.findByDriveId(driveId)
    }


    fun getAllImages(
        mediaName: String,
        mediaType: String,
        ownerId: String,
        microServiceName: String,
        offsetToken: String? = null,
        considerMaxDateRange: Boolean,
        dateRangeType: String? = null,
        giveCount: Boolean,
        limit: Int,
        giveData: Boolean,
        toRetFilterOption: ArrayList<FilterOption>
    ) : PaginationResponse<MediaDataMinifiedImpl>{
        var offsetDateFinal: ZonedDateTime? = null
        var offsetId = ""

        offsetToken?.let {
            val decrypted = encryptionMaster.decrypt(
                encodingUtilContract.decode(
                    it
                ),
            )
            val splits = decrypted.split("::")
            val decryptedOffsetDate =
                dateUtilContract.getZonedDateTimeFromStringUsingIsoFormatServerTimeZone(splits[0])
            if (decryptedOffsetDate.isEmpty)
                throw BadRequestException("Please provide valid offset token")
            offsetDateFinal = decryptedOffsetDate.get()
            offsetId = splits[1]
        } ?: run {
            val firstCreated =
                mediaDataAggregator.findTop1ByOrderByCreatedDateAsc()

            offsetDateFinal = if (firstCreated.data.isEmpty())
                null
            else {
                val instant = firstCreated.data[0]
                instant.let {
                    ZonedDateTime.ofInstant(it.createdDate.minusNanos(1000), ZoneId.of("UTC"))
                }
            }
        }

        offsetDateFinal ?: run {
            return PaginationResponse(
                arrayListOf(),
                filterUsed = toRetFilterOption,
            )
        }

        val toReturnAllMedia: ArrayList<MediaDataMinifiedImpl> = ArrayList()
        var giveCountData = 0L

        if (giveCount) {
            val allUserCount =
                mediaDataAggregator.findCountWithOutOffsetIdAndDate(
                    mediaName = mediaName,
                    mediaType =mediaType,
                    ownerId = ownerId,
                    microServiceName = microServiceName
                )
            giveCountData = allUserCount.data

        }
        
        if (giveData) {
            if (considerMaxDateRange && dateRangeType != null && dateRangeType == DateRangeType.MAX.name) {
                val allmedias = mediaDataAggregator.findDataWithOutOffsetIdAndDate(
                    mediaName = mediaName,
                    mediaType = mediaType,
                    ownerId = ownerId,
                    microServiceName = microServiceName
                )
                toReturnAllMedia.addAll(allmedias.data)
            } else {
                if (offsetId.isBlank()) {
                    val mediaFirstPage =
                        offsetDateFinal.let {
                            mediaDataAggregator.findDataWithOutOffsetId(
                                mediaName = mediaName,
                                mediaType = mediaType,
                                ownerId = ownerId,
                                microServiceName = microServiceName,
                                limit = limit,
                                offsetDate = offsetDateFinal!!
                            )
                        }
                    if (mediaFirstPage.data.isEmpty()) {
                        return PaginationResponse(
                            arrayListOf(),
                            recordCount = giveCountData.toInt(),
                            filterUsed = toRetFilterOption
                        )
                    }
                    toReturnAllMedia.addAll(mediaFirstPage.data)
                } else {
                    val mediaNextPageWithSameData =
                        mediaDataAggregator.findDataWithOffsetId(
                            mediaName = mediaName,
                            mediaType = mediaType,
                            ownerId = ownerId,
                            microServiceName = microServiceName,
                            limit = limit,
                            offsetId = offsetId
                        )
                    val nextPageSize = limit - mediaNextPageWithSameData.data.size
                    val mediaNextPage = mediaDataAggregator.findDataWithOutOffsetId(
                        mediaName = mediaName,
                        mediaType = mediaType,
                        ownerId = ownerId,
                        microServiceName = microServiceName,
                        limit = nextPageSize,
                        offsetDate = offsetDateFinal!!
                    )
                    toReturnAllMedia.addAll(mediaNextPageWithSameData.data)
                    toReturnAllMedia.addAll(mediaNextPage.data)
                }
            }
            if (toReturnAllMedia.isEmpty()) {
                return PaginationResponse(
                    arrayListOf(),
                    recordCount = giveCountData.toInt(),
                    filterUsed = toRetFilterOption
                )
            } else {
                return PaginationResponse(
                    ConverterStringToObjectList.sanitizeForOutput(ArrayList(toReturnAllMedia)),
                    filterUsed = toRetFilterOption,
                    offsetToken = encodingUtilContract.encode(
                        encryptionMaster.encrypt(
                            "${
                                toReturnAllMedia.last().createdDate
                            }::${
                                toReturnAllMedia.last().id
                            }"
                        ),
                    ),
                    recordCount = giveCountData.toInt()
                )
            }
        } else {
            return PaginationResponse(
                filterUsed = toRetFilterOption,
                recordCount = giveCountData.toInt()
            )
        }
    }
}