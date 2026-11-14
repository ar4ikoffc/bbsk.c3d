package io.github.bbsk.c5d.controller

import io.github.bbsk.c5d.model.user.dto.request.LoginRequest
import io.github.bbsk.c5d.model.user.dto.request.RegisterRequest
import io.github.bbsk.c5d.model.user.dto.response.AuthResponse
import io.github.bbsk.c5d.model.user.dto.response.MessageResponse
import io.github.bbsk.c5d.model.user.dto.response.UserResponse
import io.github.bbsk.c5d.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.security.Principal

@Tag(name = "Аутентификация", description = "Управление сессиями пользователей, вход, выход и тестовая регистрация")
@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @Operation(
        summary = "Вход в систему (Login)",
        description = "Аутентифицирует пользователя по логину и паролю. При успешной проверке устанавливает JWT токен в HttpOnly cookie 'access_token' со сроком действия 24 часа."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Успешная аутентификация",
                content = [Content(schema = Schema(implementation = AuthResponse::class))]),
            ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных"),
            ApiResponse(responseCode = "401", description = "Неверный логин или пароль")
        ]
    )
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        val (cookie, authResponse) = authService.login(request)
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(authResponse)
    }

    @Operation(
        summary = "Регистрация нового пользователя (ТОЛЬКО ДЛЯ ТЕСТИРОВАНИЯ)",
        description = "ВНИМАНИЕ: Данная ручка предназначена исключительно для целей тестирования, локальной разработки и первичной инициализации учетных записей. В боевом контуре создание пользователей ограничено."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Пользователь успешно создан",
                content = [Content(schema = Schema(implementation = UserResponse::class))]),
            ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных"),
            ApiResponse(responseCode = "409", description = "Пользователь с указанным логином уже существует")
        ]
    )
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<UserResponse> {
        val createdUser = authService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser)
    }

    @Operation(
        summary = "Выход из системы (Logout)",
        description = "Завершает сессию текущего пользователя, сбрасывая cookie 'access_token' (устанавливает max-age = 0)."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Успешный выход из системы",
                content = [Content(schema = Schema(implementation = MessageResponse::class))])
        ]
    )
    @PostMapping("/logout")
    fun logout(): ResponseEntity<MessageResponse> {
        val cookie = authService.logout()
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(MessageResponse("Успешный выход из системы. Cookie сессии очищена."))
    }

    @Operation(
        summary = "Получение профиля текущего пользователя",
        description = "Возвращает информацию о текущем аутентифицированном пользователе на основе переданного cookie 'access_token'."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Данные текущего пользователя",
                content = [Content(schema = Schema(implementation = UserResponse::class))]),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
        ]
    )
    @GetMapping("/me")
    fun me(principal: Principal?): ResponseEntity<UserResponse> {
        if (principal == null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Пользователь не авторизован")
        }
        val user = authService.getCurrentUser(principal.name)
        return ResponseEntity.ok(user)
    }
}
