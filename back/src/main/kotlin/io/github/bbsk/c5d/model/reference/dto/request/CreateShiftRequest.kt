package io.github.bbsk.c5d.model.reference.dto.request

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.bbsk.c5d.model.reference.dto.BreakWindowDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.time.LocalTime

@Schema(description = "Запрос на создание нового графика рабочей смены")
data class CreateShiftRequest(
    @field:JsonFormat(pattern = "HH:mm")
    @field:NotNull(message = "Время начала смены обязательно")
    @field:Schema(description = "Время начала смены (HH:mm)", example = "09:00", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    val start: LocalTime,

    @field:JsonFormat(pattern = "HH:mm")
    @field:NotNull(message = "Время окончания смены обязательно")
    @field:Schema(description = "Время окончания смены (HH:mm)", example = "18:00", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    val end: LocalTime,

    @field:JsonProperty("break_duration_min")
    @field:Min(0, message = "Продолжительность обеденного перерыва не может быть отрицательной")
    @field:Schema(description = "Длительность обеденного перерыва в минутах", example = "60", defaultValue = "60")
    val breakDurationMin: Int = 60,

    @field:Valid
    @field:NotNull(message = "Окно обеденного перерыва обязательно")
    @field:JsonProperty("break_window")
    @field:Schema(description = "Интервал возможного начала обеденного перерыва", requiredMode = Schema.RequiredMode.REQUIRED)
    val breakWindow: BreakWindowDto
)
