package io.github.bbsk.c5d.model.plan.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Рекомендуемое действие по предупреждению (Alert)")
enum class AlertSuggestedAction {
    @Schema(description = "Переназначить наряд другому доступному сотруднику")
    REASSIGN,

    @Schema(description = "Перенести визит на другое время/слот")
    RESCHEDULE,

    @Schema(description = "Передать в станционное подразделение")
    TRANSFER_TO_STATION,

    @Schema(description = "Действие не требуется")
    NONE
}
