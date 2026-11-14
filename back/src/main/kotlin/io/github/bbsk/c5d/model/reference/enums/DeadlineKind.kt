package io.github.bbsk.c5d.model.reference.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Формат исчисления контрольного срока выполнения наряда")
enum class DeadlineKind {
    @Schema(description = "В пределах согласованного временного слота (SLOT)")
    SLOT,

    @Schema(description = "В часах с момента регистрации наряда (HOURS)")
    HOURS,

    @Schema(description = "В рабочих днях с момента регистрации по производственному календарю РФ (WORKING_DAYS)")
    WORKING_DAYS
}
