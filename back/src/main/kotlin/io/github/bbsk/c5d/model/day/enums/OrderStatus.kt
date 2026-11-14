package io.github.bbsk.c5d.model.day.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Статус наряда на работы")
enum class OrderStatus {
    @Schema(description = "Ожидает распределения")
    PENDING,

    @Schema(description = "Назначен в маршрут сотрудника")
    ASSIGNED,

    @Schema(description = "Выполнен")
    COMPLETED,

    @Schema(description = "Отменен клиентом или не подтвержден")
    CANCELLED,

    @Schema(description = "Не удалось назначить в текущий план")
    UNASSIGNED
}
