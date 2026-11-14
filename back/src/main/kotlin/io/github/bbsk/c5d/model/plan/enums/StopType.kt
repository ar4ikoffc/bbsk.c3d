package io.github.bbsk.c5d.model.plan.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Тип остановки в маршруте")
enum class StopType {
    @Schema(description = "Выезд из базового сервисного центра")
    DEPOT_START,

    @Schema(description = "Визит к клиенту / работы по наряду")
    VISIT,

    @Schema(description = "Обеденный перерыв")
    BREAK,

    @Schema(description = "Пополнение абонентского оборудования в сервисном центре")
    RESTOCK,

    @Schema(description = "Возвращение в базовый сервисный центр")
    DEPOT_END
}
