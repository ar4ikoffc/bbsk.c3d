package io.github.bbsk.c5d.model.reference.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min

@Schema(description = "Количество абонентского оборудования (АО) по видам")
data class EquipmentDto(
    @field:JsonProperty("ONT")
    @field:Min(0, message = "Количество ONT не может быть отрицательным")
    @field:Schema(description = "Оптический терминал (PON)", example = "3")
    val ont: Int = 0,

    @field:JsonProperty("ROUTER")
    @field:Min(0, message = "Количество роутеров не может быть отрицательным")
    @field:Schema(description = "Роутер (в т.ч. Wi-Fi, xDSL- и DOCSIS-модем)", example = "4")
    val router: Int = 0,

    @field:JsonProperty("STB")
    @field:Min(0, message = "Количество ТВ-приставок не может быть отрицательным")
    @field:Schema(description = "ТВ-приставка", example = "2")
    val stb: Int = 0
)
