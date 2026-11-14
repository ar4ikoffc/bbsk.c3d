package io.github.bbsk.c5d.model.reference.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.bbsk.c5d.model.reference.enums.AccessNodeType
import io.github.bbsk.c5d.model.reference.enums.Technology
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "Запрос на обновление данных узла доступа")
data class UpdateAccessNodeRequest(
    @field:JsonProperty("access_node_id")
    @field:Schema(description = "Уникальный строковый код узла доступа", example = "AN-0031")
    val accessNodeId: String? = null,

    @field:JsonProperty("node_type")
    @field:Schema(description = "Тип узла доступа (OLT, SWITCH, DSLAM, CMTS)", example = "OLT")
    val nodeType: AccessNodeType? = null,

    @field:Schema(description = "Технология подключения", example = "PON")
    val technology: Technology? = null,

    @field:Schema(description = "Наименование узла доступа", example = "OLT Пионерский-2 (обновлено)")
    val name: String? = null,

    @field:Schema(description = "Фактический адрес размещения узла доступа", example = "г. Екатеринбург, ул. Учителей, 22")
    val address: String? = null,

    @field:Min(-90, message = "Широта не может быть меньше -90")
    @field:Max(90, message = "Широта не может быть больше 90")
    @field:Schema(description = "Географическая широта", example = "56.8698")
    val lat: Double? = null,

    @field:Min(-180, message = "Долгота не может быть меньше -180")
    @field:Max(180, message = "Долгота не может быть больше 180")
    @field:Schema(description = "Географическая долгота", example = "60.6510")
    val lon: Double? = null
)
