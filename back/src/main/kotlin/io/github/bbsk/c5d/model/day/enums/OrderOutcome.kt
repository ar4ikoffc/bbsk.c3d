package io.github.bbsk.c5d.model.day.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Фактический исход выполнения наряда (исторические/обучающие данные)")
enum class OrderOutcome {
    @Schema(description = "Успешно выполнен на месте")
    COMPLETED,

    @Schema(description = "Передан в станционное подразделение")
    TRANSFERRED_TO_STATION,

    @Schema(description = "Отменен")
    CANCELLED,

    @Schema(description = "Перенесен на другой день/слот")
    RESCHEDULED,

    @Schema(description = "Объединен в групповую проблему (ПГП)")
    PGP_GROUPED
}
