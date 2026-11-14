package io.github.bbsk.c5d.model.reference.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.bbsk.c5d.model.reference.dto.OpeningHoursDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "Запрос на обновление параметров сервисного центра")
data class UpdateServiceCenterRequest(
    @field:Schema(description = "Новое название сервисного центра", example = "СЦ «Север-Обновленный»")
    val name: String? = null,

    @field:Schema(description = "Новый фактический адрес", example = "г. Екатеринбург, ул. Победы, 32")
    val address: String? = null,

    @field:Min(-90, message = "Широта не может быть меньше -90")
    @field:Max(90, message = "Широта не может быть больше 90")
    @field:Schema(description = "Географическая широта", example = "56.8950")
    val lat: Double? = null,

    @field:Min(-180, message = "Долгота не может быть меньше -180")
    @field:Max(180, message = "Долгота не может быть больше 180")
    @field:Schema(description = "Географическая долгота", example = "60.6110")
    val lon: Double? = null,

    @field:Valid
    @field:JsonProperty("opening_hours")
    @field:Schema(description = "Обновленный режим работы")
    val openingHours: OpeningHoursDto? = null,

    @field:JsonProperty("restock_duration_min")
    @field:Min(0, message = "Время пополнения не может быть отрицательным")
    @field:Schema(description = "Время на пополнение «рюкзака» (минуты)", example = "20")
    val restockDurationMin: Int? = null
)
