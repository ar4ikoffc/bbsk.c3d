package io.github.bbsk.c5d.model.user.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "Запрос на аутентификацию (вход) пользователя в систему")
data class LoginRequest(
    @field:NotBlank(message = "Имя пользователя обязательно для заполнения")
    @field:Schema(
        description = "Уникальное имя пользователя (логин) для входа",
        example = "ivanov_ii",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val username: String,

    @field:NotBlank(message = "Пароль обязателен для заполнения")
    @field:Schema(
        description = "Пароль учетной записи пользователя",
        example = "P@ssw0rd123!",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val password: String
)
