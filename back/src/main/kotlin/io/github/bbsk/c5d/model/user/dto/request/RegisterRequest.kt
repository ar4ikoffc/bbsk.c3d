package io.github.bbsk.c5d.model.user.dto.request

import io.github.bbsk.c5d.model.user.enums.UserRole
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(
    description = "Запрос на регистрацию нового пользователя. " +
            "ВНИМАНИЕ: Данная функциональность предназначена исключительно для целей тестирования, " +
            "локальной разработки и создания начальных учетных записей."
)
data class RegisterRequest(
    @field:NotBlank(message = "Имя пользователя обязательно для заполнения")
    @field:Size(min = 3, max = 64, message = "Имя пользователя должно содержать от 3 до 64 символов")
    @field:Schema(
        description = "Уникальное имя пользователя (логин) для создания учетной записи",
        example = "ivanov_ii",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val username: String,

    @field:NotBlank(message = "Пароль обязателен для заполнения")
    @field:Size(min = 6, max = 100, message = "Пароль должен быть длиной от 6 до 100 символов")
    @field:Schema(
        description = "Пароль учетной записи пользователя (будет захэширован с использованием BCrypt)",
        example = "P@ssw0rd123!",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val password: String,

    @field:Schema(
        description = "Системная роль пользователя (по умолчанию DISPATCHER, возможные варианты: DISPATCHER, ADMIN)",
        example = "DISPATCHER",
        defaultValue = "DISPATCHER",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    val role: UserRole = UserRole.DISPATCHER
)
