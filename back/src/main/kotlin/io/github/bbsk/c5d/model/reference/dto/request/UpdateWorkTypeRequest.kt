package io.github.bbsk.c5d.model.reference.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.bbsk.c5d.model.reference.enums.DeadlineKind
import io.github.bbsk.c5d.model.reference.enums.Profile
import io.github.bbsk.c5d.model.reference.enums.WorkTypeCode
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size

@Schema(description = "Запрос на обновление типа работ")
data class UpdateWorkTypeRequest(
    @field:Schema(description = "Код типа работ", example = "INST_PON_MKD")
    val code: WorkTypeCode? = null,

    @field:Schema(description = "Наименование типа работ", example = "Инсталляция ШПД/IPTV, МКД, PON (обновлено)")
    val name: String? = null,

    @field:JsonProperty("duration_min")
    @field:Min(1, message = "Нормативная длительность должна быть не менее 1 минуты")
    @field:Schema(description = "Нормативная длительность выполнения работы (минуты)", example = "90")
    val durationMin: Int? = null,

    @field:JsonProperty("max_start_delay_min")
    @field:Min(0, message = "Опоздание не может быть отрицательным")
    @field:Schema(description = "Максимально допустимое опоздание к началу слота (минуты)", example = "10")
    val maxStartDelayMin: Int? = null,

    @field:JsonProperty("deadline_kind")
    @field:Schema(description = "Формат исчисления контрольного срока выполнения", example = "WORKING_DAYS")
    val deadlineKind: DeadlineKind? = null,

    @field:JsonProperty("deadline_value")
    @field:Min(1, message = "Значение контрольного срока должно быть положительным")
    @field:Schema(description = "Значение контрольного срока (дней или часов)", example = "3")
    val deadlineValue: Int? = null,

    @field:JsonProperty("repeat_deadline_value")
    @field:Min(1, message = "Значение повторного контрольного срока должно быть положительным")
    @field:Schema(description = "Контрольный срок для повторных заявок (часов)", example = "12")
    val repeatDeadlineValue: Int? = null,

    @field:JsonProperty("pgp_relevant")
    @field:Schema(description = "Признак релевантности для ПГП (перерывов предоставления услуг)", example = "false")
    val pgpRelevant: Boolean? = null,

    @field:Size(min = 1, message = "Список профилей не может быть пустым, если передан")
    @field:Schema(description = "Список требуемых профилей квалификации", example = "[\"INSTALL\"]")
    val profiles: Set<Profile>? = null
)
