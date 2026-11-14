package io.github.bbsk.c5d.model.reference.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.bbsk.c5d.model.reference.dto.OpeningHoursDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(description = "Запрос на создание нового сервисного центра")
data class CreateServiceCenterRequest(
    @field:JsonProperty("service_center_id")
    @field:NotBlank(message = "Код сервисного центра (service_center_id) обязателен")
    @field:Schema(description = "Уникальный строковый код СЦ", example = "SC-01", requiredMode = Schema.RequiredMode.REQUIRED)
    val serviceCenterId: String,

    @field:NotBlank(message = "Название сервисного центра обязательно")
    @field:Schema(description = "Название сервисного центра", example = "СЦ «Север»", requiredMode = Schema.RequiredMode.REQUIRED)
    val name: String,

    @field:NotBlank(message = "Адрес сервисного центра обязателен")
    @field:Schema(description = "Фактический адрес СЦ", example = "г. Екатеринбург, ул. Победы, 30", requiredMode = Schema.RequiredMode.REQUIRED)
    val address: String,

    @field:NotNull(message = "Широта (lat) обязательна")
    @field:Min(-90, message = "Широта не может быть меньше -90")
    @field:Max(90, message = "Широта не может быть больше 90")
    @field:Schema(description = "Географическая широта", example = "56.8941", requiredMode = Schema.RequiredMode.REQUIRED)
    val lat: Double,

    @field:NotNull(message = "Долгота (lon) обязательна")
    @field:Min(-180, message = "Долгота не может быть меньше -180")
    @field:Max(180, message = "Долгота не может быть больше 180")
    @field:Schema(description = "Географическая долгота", example = "60.6105", requiredMode = Schema.RequiredMode.REQUIRED)
    val lon: Double,

    @field:Valid
    @field:NotNull(message = "Режим работы обязателен")
    @field:JsonProperty("opening_hours")
    @field:Schema(description = "Режим работы СЦ", requiredMode = Schema.RequiredMode.REQUIRED)
    val openingHours: OpeningHoursDto,

    @field:JsonProperty("restock_duration_min")
    @field:Min(0, message = "Время пополнения не может быть отрицательным")
    @field:Schema(description = "Время на пополнение «рюкзака» в СЦ (минуты)", example = "15", defaultValue = "15")
    val restockDurationMin: Int = 15
)
