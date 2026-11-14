package io.github.bbsk.c5d.model.plan.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Состояние выполнения остановки")
enum class StopStatus {
    @Schema(description = "Запланирована к выполнению")
    PLANNED,

    @Schema(description = "Выполняется в данный момент")
    IN_PROGRESS,

    @Schema(description = "Завершена")
    DONE
}
