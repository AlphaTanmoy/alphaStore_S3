package com.alphaStore.alphaS3.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
@EnableWebFluxSecurity
class AppSecurityConfig(
    private val authenticationManager: AuthenticationManagerJWT,
    private val securityContextRepository: SecurityContextRepository,
) : WebFluxConfigurer {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .cors { httpSecurityCorsConfig ->
                httpSecurityCorsConfig.configurationSource { _ ->
                    val corsConfiguration = CorsConfiguration()
                    corsConfiguration.allowedOrigins = listOf(
                        "http://localhost:4200",
                        "https://dev.app.stbl1.link",
                    )
                    corsConfiguration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    corsConfiguration.allowedHeaders = listOf("*")
                    corsConfiguration
                }
            }
            .csrf { csrf -> csrf.disable() }
            .formLogin { formLogin -> formLogin.disable() }
            .httpBasic { httpBasic -> httpBasic.disable() }
            .logout { logout -> logout.disable() }
            .authenticationManager(authenticationManager)
            .securityContextRepository(securityContextRepository)
            .authorizeExchange { authorizeExchange ->
                authorizeExchange.pathMatchers("/**").permitAll()
                    .anyExchange().authenticated()
            }
            .build()
    }
}