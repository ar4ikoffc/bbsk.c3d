package io.github.bbsk.c5d.model.reference.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.bbsk.c5d.model.reference.enums.AccessNodeType
import io.github.bbsk.c5d.model.reference.enums.Technology
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(description = "Запрос на создание нового узла доступа")
data class CreateAccessNodeRequest(
    @field:JsonProperty("access_node_id")
    @field:NotBlank(message = "Код узла доступа (access_node_id) обязателен")
    @field:Schema(description = "Уникальный строковый код узла доступа", example = "AN-0031", requiredMode = Schema.RequiredMode.REQUIRED)
    val accessNodeId: String,

    @field:JsonProperty("node_type")
    @field:NotNull(message = "Тип узла доступа обязателен")
    @field:Schema(description = "Тип узла доступа (OLT, SWITCH, DSLAM, CMTS)", example = "OLT", requiredMode = Schema.RequiredMode.REQUIRED)
    val nodeType: AccessNodeType,

    @field:NotNull(message = "Технология обязательна")
    @field:Schema(description = "Технология подключения (PON, FTTB, XDSL, DOCSIS)", example = "PON", requiredMode = Schema.RequiredMode.REQUIRED)
    val technology: Technology,

    @field:NotBlank(message = "Наименование узла доступа обязательно")
    @field:Schema(description = "Наименование узла доступа", example = "OLT Пионерский-2", requiredMode = Schema.RequiredMode.REQUIRED)
    val name: String,

    @field:NotBlank(message = "Адрес размещения узла обязателен")
    @field:Schema(description = "Фактический адрес размещения узла доступа", example = "г. Екатеринбург, ул. Учителей, 22", requiredMode = Schema.RequiredMode.REQUIRED)
    val address: String,

    @field:NotNull(message = "Широта (lat) обязательна")
    @field:Min(-90, message = "Широта не может быть меньше -90")
    @field:Max(90, message = "Широта не может быть больше 90")
    @field:Schema(description = "Географическая широта", example = "56.8698", requiredMode = Schema.RequiredMode.REQUIRED)
    val lat: Double,

    @field:NotNull(message = "Долгота (lon) обязательна")
    @field:Min(-180, message = "Долгота не может быть меньше -180")
    @field:Max(180, message = "Долгота не может быть больше 180")
    @field:Schema(description = "Географическая долгота", example = "60.6510", requiredMode = Schema.RequiredMode.REQUIRED)
    val lon: Double
)
