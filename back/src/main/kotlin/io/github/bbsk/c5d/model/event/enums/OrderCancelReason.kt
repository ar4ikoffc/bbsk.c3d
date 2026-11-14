package io.github.bbsk.c5d.model.event.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Причина отмены наряда")
enum class OrderCancelReason {
    @Schema(description = "Абонент не подтвердил визит при предварительном прозвоне")
    NOT_CONFIRMED,

    @Schema(description = "Отказ по инициативе клиента")
    CUSTOMER_REQUEST
}
