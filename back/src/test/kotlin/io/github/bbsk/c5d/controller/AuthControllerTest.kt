package io.github.bbsk.c5d.controller

import io.github.bbsk.c5d.config.JwtAuthenticationFilter
import io.github.bbsk.c5d.config.JwtService
import io.github.bbsk.c5d.config.SecurityConfig
import io.github.bbsk.c5d.exception.GlobalExceptionHandler
import io.github.bbsk.c5d.model.user.dto.response.AuthResponse
import io.github.bbsk.c5d.model.user.dto.response.UserResponse
import io.github.bbsk.c5d.model.user.enums.UserRole
import io.github.bbsk.c5d.service.AuthService
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.Duration
import java.time.OffsetDateTime

@WebMvcTest(AuthController::class)
@Import(GlobalExceptionHandler::class, SecurityConfig::class, JwtAuthenticationFilter::class)
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var authService: AuthService

    @MockitoBean
    private lateinit var jwtService: JwtService

    private val sampleUserResponse = UserResponse(
        username = "ivanov_ii",
        role = UserRole.DISPATCHER,
        createdAt = OffsetDateTime.now()
    )

    @Test
    fun `POST register creates user and returns 201 without exposing uuid`() {
        `when`(authService.register(any())).thenReturn(sampleUserResponse)

        val jsonBody = """
            {
                "username": "ivanov_ii",
                "password": "StrongPassword123!",
                "role": "DISPATCHER"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.username").value("ivanov_ii"))
            .andExpect(jsonPath("$.role").value("DISPATCHER"))
            .andExpect(jsonPath("$.id").doesNotExist())
    }

    @Test
    fun `POST login returns 200 and sets HttpOnly cookie`() {
        val cookie = ResponseCookie.from("access_token", "jwt.token.value")
            .httpOnly(true)
            .path("/")
            .maxAge(Duration.ofDays(1))
            .sameSite("Lax")
            .build()

        val authResponse = AuthResponse(
            user = sampleUserResponse,
            message = "Авторизация успешно выполнена"
        )

        `when`(authService.login(any())).thenReturn(Pair(cookie, authResponse))

        val jsonBody = """
            {
                "username": "ivanov_ii",
                "password": "StrongPassword123!"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
        )
            .andExpect(status().isOk)
            .andExpect(header().exists(HttpHeaders.SET_COOKIE))
            .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("HttpOnly")))
            .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("access_token=jwt.token.value")))
            .andExpect(jsonPath("$.user.username").value("ivanov_ii"))
            .andExpect(jsonPath("$.user.id").doesNotExist())
    }

    @Test
    fun `POST logout returns 200 and clears cookie`() {
        val cookie = ResponseCookie.from("access_token", "")
            .httpOnly(true)
            .path("/")
            .maxAge(0)
            .sameSite("Lax")
            .build()

        `when`(authService.logout()).thenReturn(cookie)

        mockMvc.perform(post("/api/auth/logout"))
            .andExpect(status().isOk)
            .andExpect(header().exists(HttpHeaders.SET_COOKIE))
            .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("Max-Age=0")))
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    @WithMockUser(username = "ivanov_ii", roles = ["DISPATCHER"])
    fun `GET me with authenticated principal returns user profile`() {
        `when`(authService.getCurrentUser("ivanov_ii")).thenReturn(sampleUserResponse)

        mockMvc.perform(get("/api/auth/me"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.username").value("ivanov_ii"))
            .andExpect(jsonPath("$.role").value("DISPATCHER"))
            .andExpect(jsonPath("$.id").doesNotExist())
    }

    @Test
    fun `GET me without principal returns 401 unauthorized`() {
        mockMvc.perform(get("/api/auth/me"))
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"))
    }
}
