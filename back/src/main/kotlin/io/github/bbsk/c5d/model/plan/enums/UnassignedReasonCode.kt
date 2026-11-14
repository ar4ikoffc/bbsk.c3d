package io.github.bbsk.c5d.model.plan.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Причина невключения наряда в маршруты плана")
enum class UnassignedReasonCode {
    @Schema(description = "На смене нет сотрудника с нужным профилем, технологией или допуском к высоте")
    NO_QUALIFIED_STAFF,

    @Schema(description = "У подходящих сотрудников не хватает рабочего времени смены")
    NO_SHIFT_TIME,

    @Schema(description = "Ни один подходящий сотрудник не успевает начать визит в согласованный слот")
    SLOT_UNREACHABLE,

    @Schema(description = "Нет требуемого оборудования, а пополнение (RESTOCK) не помещается в маршрут")
    NO_EQUIPMENT,

    @Schema(description = "Исполнитель выбыл, замену найти не удалось")
    STAFF_UNAVAILABLE,

    @Schema(description = "Другая причина (подробности в тексте reason_text)")
    OTHER
}
