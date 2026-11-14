package io.github.bbsk.c5d.model.user.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Системная роль пользователя в приложении")
enum class UserRole {
    @Schema(description = "Диспетчер — основной оператор системы")
    DISPATCHER,

    @Schema(description = "Администратор системы")
    ADMIN
}
