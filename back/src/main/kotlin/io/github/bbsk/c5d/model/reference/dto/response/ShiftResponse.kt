package io.github.bbsk.c5d.model.reference.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.bbsk.c5d.model.reference.dto.BreakWindowDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalTime
import java.util.UUID

@Schema(description = "Информация о графике рабочей смены")
data class ShiftResponse(
    @field:Schema(description = "Уникальный идентификатор смены (UUID)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    val id: UUID,

    @field:JsonFormat(pattern = "HH:mm")
    @field:Schema(description = "Время начала смены (HH:mm)", example = "09:00", type = "string")
    val start: LocalTime,

    @field:JsonFormat(pattern = "HH:mm")
    @field:Schema(description = "Время окончания смены (HH:mm)", example = "18:00", type = "string")
    val end: LocalTime,

    @field:JsonProperty("break_duration_min")
    @field:Schema(description = "Длительность обеденного перерыва в минутах", example = "60")
    val breakDurationMin: Int,

    @field:JsonProperty("break_window")
    @field:Schema(description = "Интервал возможного начала обеденного перерыва")
    val breakWindow: BreakWindowDto
)
