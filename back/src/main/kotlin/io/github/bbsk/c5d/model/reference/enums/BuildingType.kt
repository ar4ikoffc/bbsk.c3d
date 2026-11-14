package io.github.bbsk.c5d.model.reference.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Тип здания / объекта")
enum class BuildingType {
    @Schema(description = "Многоквартирный дом")
    MKD,

    @Schema(description = "Частный дом")
    PRIVATE
}
