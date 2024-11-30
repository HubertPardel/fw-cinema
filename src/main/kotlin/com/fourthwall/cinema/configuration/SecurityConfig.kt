package com.fourthwall.cinema.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun userDetailsService(passwordEncoder: PasswordEncoder): InMemoryUserDetailsManager {
        val user: UserDetails = User.withUsername("user")
            .password(passwordEncoder.encode("password"))
            .roles("USER")
            .build()

        val admin: UserDetails = User.withUsername("admin")
            .password(passwordEncoder.encode("admin"))
            .roles("ADMIN", "USER")
            .build()

        return InMemoryUserDetailsManager(user, admin)
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            cors { disable() }
            csrf { disable() }
            securityMatcher("/v1/**")
            authorizeHttpRequests {
                authorize("/v1/movies**", hasAnyRole("ADMIN", "USER"))
                authorize("/v1/reviews**", hasAnyRole("ADMIN", "USER"))
                authorize(HttpMethod.GET, "/v1/showtimes**", hasAnyRole("ADMIN", "USER"))
                authorize(HttpMethod.POST, "/v1/showtimes**", hasRole("ADMIN"))
                authorize(HttpMethod.PATCH, "/v1/showtimes/*", hasRole("ADMIN"))
                authorize(anyRequest, authenticated)
            }
            httpBasic {}
        }
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        val encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
        return encoder
    }
}