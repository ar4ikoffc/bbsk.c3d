package io.github.bbsk.c5d.model.user.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Стандартный ответ с информационным сообщением")
data class MessageResponse(
    @field:Schema(
        description = "Текстовое сообщение о результате выполнения операции",
        example = "Вы успешно вышли из системы. Cookie очищена."
    )
    val message: String
)
