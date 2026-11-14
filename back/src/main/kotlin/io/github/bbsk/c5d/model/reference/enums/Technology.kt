package io.github.bbsk.c5d.model.reference.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Технология подключения абонента")
enum class Technology {
    @Schema(description = "Пассивная оптическая сеть (PON)")
    PON,

    @Schema(description = "Оптика до здания / Ethernet (FTTB)")
    FTTB,

    @Schema(description = "Цифровая абонентская линия (xDSL)")
    XDSL,

    @Schema(description = "Кабельный интернет (DOCSIS)")
    DOCSIS
}
