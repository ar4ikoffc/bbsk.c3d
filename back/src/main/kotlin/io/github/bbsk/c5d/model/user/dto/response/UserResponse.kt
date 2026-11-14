package io.github.bbsk.c5d.model.user.dto.response

import io.github.bbsk.c5d.model.user.enums.UserRole
import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime
import java.util.UUID

@Schema(description = "Данные профиля пользователя")
data class UserResponse(
    @field:Schema(
        description = "Уникальный идентификатор пользователя (UUID)",
        example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d"
    )
    val id: UUID,

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
