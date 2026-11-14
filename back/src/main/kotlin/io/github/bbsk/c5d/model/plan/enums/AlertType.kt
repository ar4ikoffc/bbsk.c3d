package io.github.bbsk.c5d.model.plan.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Тип системного предупреждения (Alert)")
enum class AlertType {
    @Schema(description = "Риск опоздания к клиенту в согласованный слот")
    LATE_RISK,

    @Schema(description = "Обнаружено подозрение на групповую проблему (ПГП)")
    PGP_DETECTED,

    @Schema(description = "Наряд не распределен в маршрут")
    UNASSIGNED
}
