package io.github.bbsk.c5d.model.event.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Тип внешнего события в течение смены")
enum class EventType {
    @Schema(description = "Зарегистрирован новый срочный наряд со слотом сегодня")
    NEW_ORDER,

    @Schema(description = "Абонент не подтвердил визит при звонке или отказался")
    ORDER_CANCELLED,

    @Schema(description = "Работы на объекте затягиваются сверх норматива (задержка)")
    VISIT_DELAYED,

    @Schema(description = "Повреждение вне зоны ответственности: наряд передан в профильное подразделение")
    ORDER_TRANSFERRED,

    @Schema(description = "Сотрудник временно или полностью недоступен (поломка, болезнь и т.д.)")
    STAFF_UNAVAILABLE,

    @Schema(description = "Ход модельного времени без изменения состава нарядов")
    TIME_TICK
}
