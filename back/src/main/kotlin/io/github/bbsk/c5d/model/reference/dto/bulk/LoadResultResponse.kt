package io.github.bbsk.c5d.model.reference.dto.bulk

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Результат пакетной загрузки данных")
data class LoadResultResponse(
    @field:Schema(description = "Статус загрузки", example = "loaded", requiredMode = Schema.RequiredMode.REQUIRED)
    val status: String = "loaded",

    @field:Schema(
        description = "Количество загруженных объектов по типам",
        example = "{\"service_centers\": 5, \"staff\": 40, \"access_nodes\": 142, \"buildings\": 1260, \"work_types\": 8}",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val counts: Map<String, Int>
)
