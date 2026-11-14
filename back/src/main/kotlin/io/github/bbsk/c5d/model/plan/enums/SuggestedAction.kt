package io.github.bbsk.c5d.model.plan.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Рекомендуемое действие по нераспределенному наряду")
enum class SuggestedAction {
    @Schema(description = "Перенос визита на другой временной слот")
    RESCHEDULE,

    @Schema(description = "Передать наряд другому подразделению или смене")
    REASSIGN,

    @Schema(description = "Эскалация диспетчеру для ручного решения")
    ESCALATE
}
