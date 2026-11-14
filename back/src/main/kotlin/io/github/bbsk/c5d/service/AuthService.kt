package io.github.bbsk.c5d.service

import io.github.bbsk.c5d.config.JwtService
import io.github.bbsk.c5d.model.user.dto.request.LoginRequest
import io.github.bbsk.c5d.model.user.dto.request.RegisterRequest
import io.github.bbsk.c5d.model.user.dto.response.AuthResponse
import io.github.bbsk.c5d.model.user.dto.response.UserResponse
import io.github.bbsk.c5d.model.user.entity.AppUser
import io.github.bbsk.c5d.repository.AppUserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Duration
import java.time.OffsetDateTime
import java.util.UUID

@Service
class AuthService(
    private val userRepository: AppUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {

    companion object {
        const val ACCESS_TOKEN_COOKIE_NAME = "access_token"
        val COOKIE_DURATION: Duration = Duration.ofDays(1)
    }

    @Transactional
    fun register(request: RegisterRequest): UserResponse {
        val trimmedUsername = request.username.trim()
        if (userRepository.existsByUsername(trimmedUsername)) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Пользователь с именем '$trimmedUsername' уже существует"
            )
        }

        val newUser = AppUser(
            id = UUID.randomUUID(),
            username = trimmedUsername,
            passwordHash = passwordEncoder.encode(request.password)!!,
            role = request.role,
            createdAt = OffsetDateTime.now()
        )

        val saved = userRepository.save(newUser)
        return saved.toDto()
    }

    @Transactional(readOnly = true)
    fun login(request: LoginRequest): Pair<ResponseCookie, AuthResponse> {
        val trimmedUsername = request.username.trim()
        val user = userRepository.findByUsername(trimmedUsername)
            ?: throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Неверное имя пользователя или пароль"
            )

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Неверное имя пользователя или пароль"
            )
        }

        val token = jwtService.generateToken(
            username = user.username,
            claims = mapOf(
                "userId" to user.id.toString(),
                "role" to user.role.name
            )
        )

        val cookie = createAccessTokenCookie(token)
        val authResponse = AuthResponse(
            user = user.toDto(),
            message = "Аутентификация успешно выполнена"
        )

        return Pair(cookie, authResponse)
    }

    fun logout(): ResponseCookie {
        return createLogoutCookie()
    }

    @Transactional(readOnly = true)
    fun getCurrentUser(username: String): UserResponse {
        val user = userRepository.findByUsername(username)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Пользователь '$username' не найден"
            )
        return user.toDto()
    }

    fun createAccessTokenCookie(token: String): ResponseCookie {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, token)
            .httpOnly(true)
            .secure(false) // false для поддержки локальной разработки по HTTP
            .path("/")
            .maxAge(COOKIE_DURATION)
            .sameSite("Lax")
            .build()
    }

    fun createLogoutCookie(): ResponseCookie {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, "")
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(0)
            .sameSite("Lax")
            .build()
    }

    private fun AppUser.toDto(): UserResponse {
        return UserResponse(
            username = this.username,
            role = this.role,
            createdAt = this.createdAt
        )
    }
}
