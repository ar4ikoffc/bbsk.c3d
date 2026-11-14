package io.github.bbsk.c5d.model.reference.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.bbsk.c5d.model.reference.dto.OpeningHoursDto
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "Информация о сервисном центре")
data class ServiceCenterResponse(
    @field:Schema(description = "Уникальный первичный ключ СЦ (UUID)", example = "b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e")
    val id: UUID,

    @field:JsonProperty("service_center_id")
    @field:Schema(description = "Строковый код сервисного центра", example = "SC-01")
    val serviceCenterId: String,

    @field:Schema(description = "Название сервисного центра", example = "СЦ «Север»")
    val name: String,

    @field:Schema(description = "Фактический адрес СЦ", example = "г. Екатеринбург, ул. Победы, 30")
    val address: String,

    @field:Schema(description = "Географическая широта", example = "56.8941")
    val lat: Double,

    @field:Schema(description = "Географическая долгота", example = "60.6105")
    val lon: Double,

    @field:JsonProperty("opening_hours")
    @field:Schema(description = "Режим работы, в котором доступно пополнение АО")
    val openingHours: OpeningHoursDto,

    @field:JsonProperty("restock_duration_min")
    @field:Schema(description = "Время на пополнение «рюкзака» в СЦ, мин", example = "15")
    val restockDurationMin: Int
)
