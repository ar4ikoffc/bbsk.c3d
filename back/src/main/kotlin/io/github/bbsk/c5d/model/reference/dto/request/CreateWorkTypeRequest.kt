package io.github.bbsk.c5d.model.reference.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.bbsk.c5d.model.reference.enums.DeadlineKind
import io.github.bbsk.c5d.model.reference.enums.Profile
import io.github.bbsk.c5d.model.reference.enums.WorkTypeCode
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

@Schema(description = "Запрос на создание нового типа работ")
data class CreateWorkTypeRequest(
    @field:NotNull(message = "Код типа работ обязателен")
    @field:Schema(description = "Код типа работ", example = "INST_PON_MKD", requiredMode = Schema.RequiredMode.REQUIRED)
    val code: WorkTypeCode,

    @field:NotBlank(message = "Наименование типа работ обязательно")
    @field:Schema(description = "Наименование типа работ", example = "Инсталляция ШПД/IPTV, МКД, PON", requiredMode = Schema.RequiredMode.REQUIRED)
    val name: String,

    @field:JsonProperty("duration_min")
    @field:NotNull(message = "Нормативная длительность обязательна")
    @field:Min(1, message = "Нормативная длительность должна быть не менее 1 минуты")
    @field:Schema(description = "Нормативная длительность выполнения работы (минуты)", example = "90", requiredMode = Schema.RequiredMode.REQUIRED)
    val durationMin: Int,

    @field:JsonProperty("max_start_delay_min")
    @field:Min(0, message = "Опоздание не может быть отрицательным")
    @field:Schema(description = "Максимально допустимое опоздание к началу слота (минуты)", example = "10")
    val maxStartDelayMin: Int? = null,

    @field:JsonProperty("deadline_kind")
    @field:NotNull(message = "Формат исчисления контрольного срока обязателен")
    @field:Schema(description = "Формат исчисления контрольного срока выполнения", example = "WORKING_DAYS", requiredMode = Schema.RequiredMode.REQUIRED)
    val deadlineKind: DeadlineKind,

    @field:JsonProperty("deadline_value")
    @field:Min(1, message = "Значение контрольного срока должно быть положительным")
    @field:Schema(description = "Значение контрольного срока (дней или часов)", example = "3")
    val deadlineValue: Int? = null,

    @field:JsonProperty("repeat_deadline_value")
    @field:Min(1, message = "Значение повторного контрольного срока должно быть положительным")
    @field:Schema(description = "Контрольный срок для повторных заявок (часов)", example = "12")
    val repeatDeadlineValue: Int? = null,

    @field:JsonProperty("pgp_relevant")
    @field:Schema(description = "Признак релевантности для ПГП (перерывов предоставления услуг)", example = "false", defaultValue = "false")
    val pgpRelevant: Boolean = false,

    @field:NotEmpty(message = "Должен быть указан хотя бы один квалификационный профиль")
    @field:Schema(description = "Список требуемых профилей квалификации", example = "[\"INSTALL\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    val profiles: Set<Profile>
)
