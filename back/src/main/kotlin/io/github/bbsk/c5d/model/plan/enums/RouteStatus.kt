package io.github.bbsk.c5d.model.plan.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Статус маршрута сотрудника")
enum class RouteStatus {
    @Schema(description = "Активный маршрут")
    ACTIVE,

    @Schema(description = "Сотрудник временно или полностью недоступен (по событию STAFF_UNAVAILABLE)")
    UNAVAILABLE
}
