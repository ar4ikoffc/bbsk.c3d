package io.github.bbsk.c5d.model.reference.dto.bulk

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.bbsk.c5d.model.reference.enums.BuildingType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(description = "Здание в составе узла доступа для пакетной загрузки")
data class ReferenceBuildingDto(
    @field:JsonProperty("building_id")
    @field:NotBlank(message = "Код здания (building_id) обязателен")
    @field:Schema(description = "Уникальный строковый код здания", example = "B-00412", requiredMode = Schema.RequiredMode.REQUIRED)
    val buildingId: String,

    @field:NotBlank(message = "Адрес здания обязателен")
    @field:Schema(description = "Фактический адрес здания", example = "г. Екатеринбург, ул. Учителей, 18", requiredMode = Schema.RequiredMode.REQUIRED)
    val address: String,

    @field:NotNull(message = "Широта (lat) обязательна")
    @field:Min(-90, message = "Широта не может быть меньше -90")
    @field:Max(90, message = "Широта не может быть больше 90")
    @field:Schema(description = "Географическая широта", example = "56.8712", requiredMode = Schema.RequiredMode.REQUIRED)
    val lat: Double,

    @field:NotNull(message = "Долгота (lon) обязательна")
    @field:Min(-180, message = "Долгота не может быть меньше -180")
    @field:Max(180, message = "Долгота не может быть больше 180")
    @field:Schema(description = "Географическая долгота", example = "60.6531", requiredMode = Schema.RequiredMode.REQUIRED)
    val lon: Double,

    @field:JsonProperty("building_type")
    @field:NotNull(message = "Тип здания обязателен")
    @field:Schema(description = "Тип здания (MKD / PRIVATE)", example = "MKD", requiredMode = Schema.RequiredMode.REQUIRED)
    val buildingType: BuildingType,

    @field:JsonProperty("overhead_line")
    @field:Schema(description = "Признак подключения по воздушной линии связи (ВЛС)", example = "false", defaultValue = "false")
    val overheadLine: Boolean = false,

    @field:JsonProperty("zone_service_center_id")
    @field:Schema(description = "Код СЦ, в зону которого входит дом (например, SC-01)", example = "SC-01")
    val zoneServiceCenterId: String? = null
)
