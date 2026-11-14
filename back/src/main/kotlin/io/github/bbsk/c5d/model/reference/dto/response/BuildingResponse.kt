package io.github.bbsk.c5d.model.reference.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.bbsk.c5d.model.reference.enums.BuildingType
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "Информация о здании / адресе обслуживания")
data class BuildingResponse(
    @field:Schema(description = "Уникальный идентификатор здания (UUID)", example = "4ba85f64-5717-4562-b3fc-2c963f66afa7", requiredMode = Schema.RequiredMode.REQUIRED)
    val id: UUID,

    @field:JsonProperty("building_id")
    @field:Schema(description = "Код здания", example = "B-00412", requiredMode = Schema.RequiredMode.REQUIRED)
    val buildingId: String,

    @field:JsonProperty("access_node_id")
    @field:Schema(description = "UUID узла доступа связи", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", requiredMode = Schema.RequiredMode.REQUIRED)
    val accessNodeId: UUID,

    @field:JsonProperty("access_node_code")
    @field:Schema(description = "Код узла доступа связи", example = "AN-0031")
    val accessNodeCode: String? = null,

    @field:JsonProperty("zone_service_center_id")
    @field:Schema(description = "UUID сервисного центра, в чьей зоне находится здание", example = "b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e")
    val zoneServiceCenterId: UUID? = null,

    @field:JsonProperty("zone_service_center_code")
    @field:Schema(description = "Код сервисного центра зоны", example = "SC-01")
    val zoneServiceCenterCode: String? = null,

    @field:Schema(description = "Адрес здания", example = "г. Екатеринбург, ул. Учителей, 18", requiredMode = Schema.RequiredMode.REQUIRED)
    val address: String,

    @field:Schema(description = "Географическая широта", example = "56.8712", requiredMode = Schema.RequiredMode.REQUIRED)
    val lat: Double,

    @field:Schema(description = "Географическая долгота", example = "60.6531", requiredMode = Schema.RequiredMode.REQUIRED)
    val lon: Double,

    @field:JsonProperty("building_type")
    @field:Schema(description = "Тип здания (MKD / PRIVATE)", example = "MKD", requiredMode = Schema.RequiredMode.REQUIRED)
    val buildingType: BuildingType,

    @field:JsonProperty("overhead_line")
    @field:Schema(description = "Признак подключения по воздушной линии связи (ВЛС)", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
    val overheadLine: Boolean
)
