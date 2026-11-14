package io.github.bbsk.c5d.model.reference.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.bbsk.c5d.model.reference.enums.AccessNodeType
import io.github.bbsk.c5d.model.reference.enums.Technology
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "Информация об узле доступа связи")
data class AccessNodeResponse(
    @field:Schema(description = "Уникальный идентификатор узла доступа (UUID)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", requiredMode = Schema.RequiredMode.REQUIRED)
    val id: UUID,

    @field:JsonProperty("access_node_id")
    @field:Schema(description = "Код узла доступа", example = "AN-0031", requiredMode = Schema.RequiredMode.REQUIRED)
    val accessNodeId: String,

    @field:JsonProperty("node_type")
    @field:Schema(description = "Тип узла доступа (OLT, SWITCH, DSLAM, CMTS)", example = "OLT", requiredMode = Schema.RequiredMode.REQUIRED)
    val nodeType: AccessNodeType,

    @field:Schema(description = "Технология подключения", example = "PON", requiredMode = Schema.RequiredMode.REQUIRED)
    val technology: Technology,

    @field:Schema(description = "Наименование узла доступа", example = "OLT Пионерский-2", requiredMode = Schema.RequiredMode.REQUIRED)
    val name: String,

    @field:Schema(description = "Адрес размещения узла доступа", example = "г. Екатеринбург, ул. Учителей, 22", requiredMode = Schema.RequiredMode.REQUIRED)
    val address: String,

    @field:Schema(description = "Географическая широта", example = "56.8698", requiredMode = Schema.RequiredMode.REQUIRED)
    val lat: Double,

    @field:Schema(description = "Географическая долгота", example = "60.6510", requiredMode = Schema.RequiredMode.REQUIRED)
    val lon: Double
)
