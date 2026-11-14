package io.github.bbsk.c5d.model.common.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Формат ответа об ошибке в соответствии со спецификацией API")
data class ErrorResponse(
    @field:Schema(
        description = "Тело ошибки",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val error: ErrorBody
) {
    companion object {
        fun of(code: String, message: String, details: List<ErrorDetail> = emptyList()): ErrorResponse {
            return ErrorResponse(
                error = ErrorBody(
                    code = code,
                    message = message,
                    details = details
                )
            )
        }
    }
}

@Schema(description = "Детализированная информация об ошибке")
data class ErrorBody(
    @field:Schema(
        description = "Машинно-читаемый код ошибки (например: BAD_REQUEST, PLAN_NOT_FOUND, EVENT_OUT_OF_ORDER, UNKNOWN_ORDER)",
        example = "BAD_REQUEST",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val code: String,

    @field:Schema(
        description = "Человеко-читаемое текстовое описание ошибки",
        example = "Поле slot.end: ожидается date-time",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val message: String,

    @field:Schema(
        description = "Список детальных сообщений по отдельным атрибутам",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    val details: List<ErrorDetail> = emptyList()
)

@Schema(description = "Описание ошибки конкретного поля или атрибута")
data class ErrorDetail(
    @field:Schema(
        description = "Путь к полю с ошибкой (если применимо)",
        example = "orders[12].slot.end",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    val field: String? = null,

    @field:Schema(
        description = "Текст сообщения об ошибке поля",
        example = "ожидается date-time",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val message: String
)
