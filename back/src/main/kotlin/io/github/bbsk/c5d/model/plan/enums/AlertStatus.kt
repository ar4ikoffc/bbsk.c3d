package io.github.bbsk.c5d.model.plan.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Статус предупреждения")
enum class AlertStatus {
    @Schema(description = "Активно (требует внимания или обработки)")
    ACTIVE,

    @Schema(description = "Разрешено / обработано")
    RESOLVED
}
