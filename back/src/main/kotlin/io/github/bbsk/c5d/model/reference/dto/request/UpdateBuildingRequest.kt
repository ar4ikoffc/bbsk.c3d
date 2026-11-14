package io.github.bbsk.c5d.model.reference.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.bbsk.c5d.model.reference.enums.BuildingType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.util.UUID

@Schema(description = "Запрос на обновление данных здания")
data class UpdateBuildingRequest(
    @field:JsonProperty("building_id")
    @field:Schema(description = "Уникальный строковый код здания", example = "B-00412")
    val buildingId: String? = null,

    @field:JsonProperty("access_node_id")
    @field:Schema(description = "Уникальный идентификатор (UUID) узла доступа", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    val accessNodeId: UUID? = null,

    @field:JsonProperty("zone_service_center_id")
    @field:Schema(description = "Уникальный идентификатор (UUID) сервисного центра зоны (передайте null для сброса)", example = "b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e")
    val zoneServiceCenterId: UUID? = null,

    @field:Schema(description = "Фактический адрес здания", example = "г. Екатеринбург, ул. Учителей, 18")
    val address: String? = null,

    @field:Min(-90, message = "Широта не может быть меньше -90")
    @field:Max(90, message = "Широта не может быть больше 90")
    @field:Schema(description = "Географическая широта", example = "56.8712")
    val lat: Double? = null,

    @field:Min(-180, message = "Долгота не может быть меньше -180")
    @field:Max(180, message = "Долгота не может быть больше 180")
    @field:Schema(description = "Географическая долгота", example = "60.6531")
    val lon: Double? = null,

    @field:JsonProperty("building_type")
    @field:Schema(description = "Тип здания (MKD / PRIVATE)", example = "MKD")
    val buildingType: BuildingType? = null,

    @field:JsonProperty("overhead_line")
    @field:Schema(description = "Признак подключения по воздушной линии связи (ВЛС)", example = "false")
    val overheadLine: Boolean? = null
)
