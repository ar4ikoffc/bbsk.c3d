package io.github.bbsk.c5d.model.reference.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.bbsk.c5d.model.reference.enums.DeadlineKind
import io.github.bbsk.c5d.model.reference.enums.Profile
import io.github.bbsk.c5d.model.reference.enums.WorkTypeCode
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "Информация о типе выполняемых работ")
data class WorkTypeResponse(
    @field:Schema(description = "Уникальный идентификатор типа работ", example = "10000000-0000-0000-0000-000000000001", requiredMode = Schema.RequiredMode.REQUIRED)
    val id: UUID,

    @field:Schema(description = "Код типа работ", example = "INST_PON_MKD", requiredMode = Schema.RequiredMode.REQUIRED)
    val code: WorkTypeCode,

    @field:Schema(description = "Наименование типа работ", example = "Инсталляция ШПД/IPTV, МКД, PON", requiredMode = Schema.RequiredMode.REQUIRED)
    val name: String,

    @field:JsonProperty("duration_min")
    @field:Schema(description = "Нормативная длительность выполнения работы (минуты)", example = "90", requiredMode = Schema.RequiredMode.REQUIRED)
    val durationMin: Int,

    @field:JsonProperty("max_start_delay_min")
    @field:Schema(description = "Максимально допустимое опоздание к началу слота (минуты)", example = "10")
    val maxStartDelayMin: Int?,

    @field:JsonProperty("deadline_kind")
    @field:Schema(description = "Формат исчисления контрольного срока выполнения", example = "WORKING_DAYS", requiredMode = Schema.RequiredMode.REQUIRED)
    val deadlineKind: DeadlineKind,

    @field:JsonProperty("deadline_value")
    @field:Schema(description = "Значение контрольного срока (дней или часов)", example = "3")
    val deadlineValue: Int?,

    @field:JsonProperty("repeat_deadline_value")
    @field:Schema(description = "Контрольный срок для повторных заявок (часов)", example = "12")
    val repeatDeadlineValue: Int?,

    @field:JsonProperty("pgp_relevant")
    @field:Schema(description = "Признак релевантности для ПГП (перерывов предоставления услуг)", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
    val pgpRelevant: Boolean,

    @field:Schema(description = "Список требуемых профилей квалификации", example = "[\"INSTALL\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    val profiles: Set<Profile>
)
