package io.github.bbsk.c5d.service

import io.github.bbsk.c5d.config.JwtService
import io.github.bbsk.c5d.model.user.dto.request.LoginRequest
import io.github.bbsk.c5d.model.user.dto.request.RegisterRequest
import io.github.bbsk.c5d.model.user.entity.AppUser
import io.github.bbsk.c5d.model.user.enums.UserRole
import io.github.bbsk.c5d.repository.AppUserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.server.ResponseStatusException
import java.time.OffsetDateTime
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class AuthServiceTest {

    @Mock
    private lateinit var userRepository: AppUserRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var jwtService: JwtService

    private lateinit var authService: AuthService

    @BeforeEach
    fun setUp() {
        authService = AuthService(userRepository, passwordEncoder, jwtService)
    }

    @Test
    fun `register successfully creates new user and does not expose uuid`() {
        val request = RegisterRequest(
            username = "dispatcher_ivanov",
            password = "SecretPassword123!",
            role = UserRole.DISPATCHER
        )

        `when`(userRepository.existsByUsername("dispatcher_ivanov")).thenReturn(false)
        `when`(passwordEncoder.encode("SecretPassword123!")).thenReturn("hashed_password")
        `when`(userRepository.save(any(AppUser::class.java))).thenAnswer { invocation ->
            invocation.getArgument(0) as AppUser
        }

        val response = authService.register(request)

        assertNotNull(response)
        assertEquals("dispatcher_ivanov", response.username)
        assertEquals(UserRole.DISPATCHER, response.role)
        verify(userRepository, times(1)).save(any(AppUser::class.java))
    }

    @Test
    fun `register throws conflict exception if username already exists`() {
        val request = RegisterRequest(
            username = "existing_user",
            password = "SecretPassword123!",
            role = UserRole.DISPATCHER
        )

        `when`(userRepository.existsByUsername("existing_user")).thenReturn(true)

        val exception = assertThrows(ResponseStatusException::class.java) {
            authService.register(request)
        }

        assertTrue(exception.statusCode.is4xxClientError)
        verify(userRepository, never()).save(any(AppUser::class.java))
    }

    @Test
    fun `login returns access token cookie and user response on valid credentials`() {
        val user = AppUser(
            id = UUID.randomUUID(),
            username = "ivanov",
            passwordHash = "encoded_hash",
            role = UserRole.DISPATCHER,
            createdAt = OffsetDateTime.now()
        )

        val request = LoginRequest(
            username = "ivanov",
            password = "correct_password"
        )

        `when`(userRepository.findByUsername("ivanov")).thenReturn(user)
        `when`(passwordEncoder.matches("correct_password", "encoded_hash")).thenReturn(true)
        `when`(jwtService.generateToken(eq("ivanov"), anyMap())).thenReturn("mocked.jwt.token")

        val (cookie, authResponse) = authService.login(request)

        assertNotNull(cookie)
        assertEquals("access_token", cookie.name)
        assertEquals("mocked.jwt.token", cookie.value)
        assertTrue(cookie.isHttpOnly)
        assertEquals("ivanov", authResponse.user.username)
        assertEquals(UserRole.DISPATCHER, authResponse.user.role)
    }

    @Test
    fun `login throws unauthorized when password does not match`() {
        val user = AppUser(
            id = UUID.randomUUID(),
            username = "ivanov",
            passwordHash = "encoded_hash",
            role = UserRole.DISPATCHER
        )

        val request = LoginRequest(
            username = "ivanov",
            password = "wrong_password"
        )

        `when`(userRepository.findByUsername("ivanov")).thenReturn(user)
        `when`(passwordEncoder.matches("wrong_password", "encoded_hash")).thenReturn(false)

        val exception = assertThrows(ResponseStatusException::class.java) {
            authService.login(request)
        }

        assertEquals(401, exception.statusCode.value())
    }

    @Test
    fun `logout returns cookie with maxAge zero`() {
        val cookie = authService.logout()

        assertEquals("access_token", cookie.name)
        assertEquals(0, cookie.maxAge.seconds)
        assertTrue(cookie.isHttpOnly)
    }
}
