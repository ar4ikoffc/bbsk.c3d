package io.github.bbsk.c5d.model.reference.dto.bulk

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.bbsk.c5d.model.reference.dto.EquipmentDto
import io.github.bbsk.c5d.model.reference.dto.ShiftDto
import io.github.bbsk.c5d.model.reference.enums.Profile
import io.github.bbsk.c5d.model.reference.enums.Technology
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

@Schema(description = "Сотрудник для пакетной загрузки")
data class ReferenceStaffDto(
    @field:JsonProperty("staff_id")
    @field:NotBlank(message = "Код сотрудника (staff_id) обязателен")
    @field:Schema(description = "Уникальный строковый код сотрудника", example = "TECH-01", requiredMode = Schema.RequiredMode.REQUIRED)
    val staffId: String,

    @field:NotBlank(message = "ФИО сотрудника обязательно")
    @field:Schema(description = "ФИО или отображаемое имя сотрудника", example = "Иванов Иван", requiredMode = Schema.RequiredMode.REQUIRED)
    val name: String,

    @field:JsonProperty("service_center_id")
    @field:NotBlank(message = "Код сервисного центра привязки обязателен")
    @field:Schema(description = "Код сервисного центра (базового депо)", example = "SC-01", requiredMode = Schema.RequiredMode.REQUIRED)
    val serviceCenterId: String,

    @field:Valid
    @field:NotNull(message = "График смены обязателен")
    @field:Schema(description = "График рабочей смены", requiredMode = Schema.RequiredMode.REQUIRED)
    val shift: ShiftDto,

    @field:NotEmpty(message = "У сотрудника должен быть указан хотя бы один профиль квалификации")
    @field:Schema(description = "Профили квалификации", example = "[\"INSTALL\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    val profiles: Set<Profile>,

    @field:NotEmpty(message = "У сотрудника должна быть указана хотя бы одна поддерживаемая технология")
    @field:Schema(description = "Технологии подключения", example = "[\"PON\", \"FTTB\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    val technologies: Set<Technology>,

    @field:Valid
    @field:JsonProperty("equipment_start")
    @field:Schema(description = "Начальный комплект оборудования («рюкзак»)")
    val equipmentStart: EquipmentDto = EquipmentDto(),

    @field:Valid
    @field:JsonProperty("equipment_capacity")
    @field:Schema(description = "Максимальная вместимость оборудования")
    val equipmentCapacity: EquipmentDto = EquipmentDto(),

    @field:JsonProperty("height_permit")
    @field:Schema(description = "Наличие допуска к работам на высоте", example = "false", defaultValue = "false")
    val heightPermit: Boolean = false,

    @field:JsonProperty("experience_years")
    @field:Schema(description = "Стаж работы сотрудника в годах", example = "0.0", defaultValue = "0.0")
    val experienceYears: Double = 0.0
)
