package com.alphaStore.alphaS3

import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class RestTemplateMaster {
    fun getRestTemplate(timeoutSeconds: Int = 20): RestTemplate {
        val simpleClientHttpRequestFactory = SimpleClientHttpRequestFactory()
        simpleClientHttpRequestFactory.setReadTimeout(timeoutSeconds * 1000)
        simpleClientHttpRequestFactory.setConnectTimeout(timeoutSeconds * 1000)
        return RestTemplate(simpleClientHttpRequestFactory)
    }
}