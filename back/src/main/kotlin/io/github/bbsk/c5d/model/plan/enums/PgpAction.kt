package io.github.bbsk.c5d.model.plan.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Действие по группе нарядов с подозрением на групповую проблему (ПГП)")
enum class PgpAction {
    @Schema(description = "Передать наряды группы в станционное подразделение")
    TRANSFER_TO_STATION
}
