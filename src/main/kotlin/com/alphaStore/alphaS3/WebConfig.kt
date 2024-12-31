package com.alphaStore.alphaS3

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")  // Allow all paths
            .allowedOrigins("http://localhost:3000") // Your frontend URL (e.g., React dev server)
            .allowedMethods("GET", "POST", "PUT", "DELETE") // Allow these HTTP methods
            .allowedHeaders("*") // Allow all headers
            .allowCredentials(true) // Allow sending cookies or authentication info
    }
}
