package io.github.bbsk.c5d.model.user.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Результат аутентификации пользователя")
data class AuthResponse(
    @field:Schema(
        description = "Данные авторизованного пользователя"
    )
    val user: UserResponse,

    @field:Schema(
        description = "Информационное сообщение о статусе операции",
        example = "Аутентификация успешно выполнена. Токен сохранен в HttpOnly cookie 'access_token'"
    )
    val message: String
)
