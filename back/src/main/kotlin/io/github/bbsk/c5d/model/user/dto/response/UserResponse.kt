package io.github.bbsk.c5d.model.user.dto.response

import io.github.bbsk.c5d.model.user.enums.UserRole
import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

@Schema(description = "Данные профиля пользователя")
data class UserResponse(
    @field:Schema(
        description = "Уникальное имя пользователя (логин)",
        example = "ivanov_ii"
    )
    val username: String,

    @field:Schema(
        description = "Системная роль пользователя",
        example = "DISPATCHER"
    )
    val role: UserRole,

    @field:Schema(
        description = "Дата и время создания учетной записи (ISO 8601)",
        example = "2026-11-14T12:00:00+05:00"
    )
    val createdAt: OffsetDateTime
)
