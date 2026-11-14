package io.github.bbsk.c5d.model.reference.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import java.time.LocalTime

@Schema(description = "Параметры рабочей смены сотрудника")
data class ShiftDto(
    @field:JsonFormat(pattern = "HH:mm")
    @field:Schema(description = "Время начала смены (HH:mm)", example = "09:00", type = "string")
    val start: LocalTime,

    @field:JsonFormat(pattern = "HH:mm")
    @field:Schema(description = "Время окончания смены (HH:mm)", example = "18:00", type = "string")
    val end: LocalTime,

    @field:JsonProperty("break_duration_min")
    @field:Min(0, message = "Продолжительность обеденного перерыва не может быть отрицательной")
    @field:Schema(description = "Длительность обеденного перерыва в минутах", example = "60")
    val breakDurationMin: Int = 60,

    @field:JsonProperty("break_window")
    @field:Schema(description = "Интервал возможного начала обеденного перерыва")
    val breakWindow: BreakWindowDto
)
