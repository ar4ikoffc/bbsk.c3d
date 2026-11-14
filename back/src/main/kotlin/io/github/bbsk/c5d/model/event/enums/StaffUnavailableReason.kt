package io.github.bbsk.c5d.model.event.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Причина недоступности сотрудника на смене")
enum class StaffUnavailableReason {
    @Schema(description = "Поломка служебного/личного транспорта")
    VEHICLE_BREAKDOWN,

    @Schema(description = "Внезапная болезнь сотрудника")
    ILLNESS,

    @Schema(description = "Иные непредвиденные обстоятельства")
    OTHER
}
