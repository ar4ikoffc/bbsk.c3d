package io.github.bbsk.c5d.model.reference.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Тип узла доступа связи")
enum class AccessNodeType {
    @Schema(description = "Оптический линейный терминал (PON)")
    OLT,

    @Schema(description = "Коммутатор доступа (FTTB)")
    SWITCH,

    @Schema(description = "Мультиплексор цифровой абонентской линии (xDSL)")
    DSLAM,

    @Schema(description = "Система окончания кабельных модемов (DOCSIS)")
    CMTS
}
