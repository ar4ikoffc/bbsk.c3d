package io.github.bbsk.c5d.model.reference.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Код типа выполняемых работ")
enum class WorkTypeCode {
    @Schema(description = "Инсталляция ШПД/IPTV, многоквартирный дом, PON")
    INST_PON_MKD,

    @Schema(description = "Инсталляция ШПД/IPTV, многоквартирный дом, FTTB")
    INST_FTTB_MKD,

    @Schema(description = "Инсталляция, частный сектор, с воздушной линией связи (ВЛС)")
    INST_PRIVATE_VLS,

    @Schema(description = "Доподключение услуги (IPTV, Wi-Fi-роутер)")
    ADD_SERVICE,

    @Schema(description = "Обследование (частный сектор)")
    SURVEY_PRIVATE,

    @Schema(description = "Устранение неисправности (обращение абонента)")
    REPAIR,

    @Schema(description = "Устранение деградации, выявленной системой мониторинга качества")
    DEGRADATION,

    @Schema(description = "Демонтаж абонентского оборудования")
    DISMANTLE
}
