package io.github.bbsk.c5d.model.reference.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Профиль квалификации выездного сотрудника")
enum class Profile {
    @Schema(description = "Инсталляция (подключение новых услуг)")
    INSTALL,

    @Schema(description = "Третья линия технической поддержки (устранение неисправностей, восстановление услуг)")
    LTP3
}
