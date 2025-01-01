package com.alphaStore.alphaS3

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/alphaS3")
class HealthCheck {

    @GetMapping("/healthCheck")
    fun healthCheck() : String {
        return "I Am Up"
    }

}