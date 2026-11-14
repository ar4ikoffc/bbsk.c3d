package io.github.bbsk.c5d.model.reference.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalTime

@Schema(description = "Интервал, в котором должен начаться обеденный перерыв")
data class BreakWindowDto(
    @field:JsonFormat(pattern = "HH:mm")
    @field:Schema(description = "Начало окна обеденного перерыва (HH:mm)", example = "12:00", type = "string")
    val from: LocalTime,

    @field:JsonFormat(pattern = "HH:mm")
    @field:Schema(description = "Окончание окна обеденного перерыва (HH:mm)", example = "15:00", type = "string")
    val to: LocalTime
)
