package io.github.bbsk.c5d.model.event.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Подразделение/зона передачи наряда")
enum class TransferredTo {
    @Schema(description = "Станционное оборудование")
    STATION_EQUIPMENT,

    @Schema(description = "Магистральный кабель")
    TRUNK_CABLE
}
