package com.alphaStore.alphaS3.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import org.springframework.stereotype.Component
import java.util.*
import com.alphaStore.alphaS3.annotation.ConditionalJsonIgnore
@Component
object ConverterStringToObjectList {

    inline fun <reified T> getObjectList(
        dataToConvert: String,
        shouldIncludeConditionalJsonIgnores: Boolean = true
    ): ArrayList<T> {
        if (dataToConvert.isEmpty())
            return ArrayList()
        val mapper = ObjectMapper()
        mapper.setTimeZone(TimeZone.getDefault())
        mapper.setAnnotationIntrospector(
            object : JacksonAnnotationIntrospector() {
                override fun hasIgnoreMarker(m: AnnotatedMember?): Boolean {
                    m?.let { positiveM ->
                        if (
                            shouldIncludeConditionalJsonIgnores &&
                            positiveM.hasAnnotation(ConditionalJsonIgnore::class.java)
                        )
                            return false
                    }
                    return super.hasIgnoreMarker(m)
                }
            }
        )
        return mapper
            .findAndRegisterModules()
            .readValue(
                dataToConvert,
                mapper
                    .typeFactory
                    .constructCollectionType(
                        ArrayList::class.java,
                        T::class.java
                    )
            )
    }

    fun convertObjectToString(
        objectToConvert: Any,
        filter: SimpleFilterProvider? = null,
        shouldIncludeConditionalJsonIgnores: Boolean = true
    ): String {
        try {
            return ObjectMapper()
                .setTimeZone(TimeZone.getDefault())
                .setAnnotationIntrospector(
                    object : JacksonAnnotationIntrospector() {
                        override fun hasIgnoreMarker(m: AnnotatedMember?): Boolean {
                            m?.let { positiveM ->
                                if (
                                    shouldIncludeConditionalJsonIgnores &&
                                    positiveM.hasAnnotation(ConditionalJsonIgnore::class.java)
                                )
                                    return false
                            }
                            return super.hasIgnoreMarker(m)
                        }
                    }
                )
                .findAndRegisterModules()
                .writer(filter)
                .writeValueAsString(objectToConvert)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return "{}"
    }

    inline fun <reified T> sanitizeForOutput(
        objectToConvert: Any,
    ): ArrayList<T> {
        return getObjectList<T>(
            convertObjectToString(
                objectToConvert,
                shouldIncludeConditionalJsonIgnores = true
            ),
            shouldIncludeConditionalJsonIgnores = false
        )
    }

}