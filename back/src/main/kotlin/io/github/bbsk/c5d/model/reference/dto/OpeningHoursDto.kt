package io.github.bbsk.c5d.model.reference.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalTime

@Schema(description = "Режим работы сервисного центра (доступность пополнения АО)")
data class OpeningHoursDto(
    @field:JsonFormat(pattern = "HH:mm")
    @field:Schema(description = "Время открытия (HH:mm)", example = "08:00", type = "string")
    val open: LocalTime,

    @field:JsonFormat(pattern = "HH:mm")
    @field:Schema(description = "Время закрытия (HH:mm)", example = "21:00", type = "string")
    val close: LocalTime
)
