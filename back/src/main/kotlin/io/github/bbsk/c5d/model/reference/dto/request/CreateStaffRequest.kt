package io.github.bbsk.c5d.model.reference.dto.request

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
import java.util.UUID

@Schema(description = "Запрос на создание нового выездного сотрудника (бригады)")
data class CreateStaffRequest(
    @field:JsonProperty("staff_id")
    @field:NotBlank(message = "Код сотрудника (staff_id) обязателен для заполнения")
    @field:Schema(description = "Уникальный строковый код сотрудника", example = "ST-001", requiredMode = Schema.RequiredMode.REQUIRED)
    val staffId: String,

    @field:JsonProperty("display_name")
    @field:NotBlank(message = "Имя сотрудника (display_name) обязательно для заполнения")
    @field:Schema(description = "Отображаемое имя сотрудника / бригады", example = "Иванов Иван Иванович", requiredMode = Schema.RequiredMode.REQUIRED)
    val displayName: String,

    @field:JsonProperty("service_center_id")
    @field:NotNull(message = "Идентификатор сервисного центра (UUID) обязателен")
    @field:Schema(description = "UUID сервисного центра прикрепления", example = "b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e", requiredMode = Schema.RequiredMode.REQUIRED)
    val serviceCenterId: UUID,

    @field:NotEmpty(message = "Необходимо указать хотя бы один рабочий профиль")
    @field:Schema(description = "Профили квалификации сотрудника", example = "[\"INSTALL\", \"LTP3\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    val profiles: Set<Profile>,

    @field:NotEmpty(message = "Необходимо указать хотя бы одну поддерживаемую технологию")
    @field:Schema(description = "Поддерживаемые технологии подключения", example = "[\"PON\", \"FTTB\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    val technologies: Set<Technology>,

    @field:JsonProperty("height_permit")
    @field:Schema(description = "Наличие допуска к работам на высоте (ВЛС)", example = "true", defaultValue = "false")
    val heightPermit: Boolean = false,

    @field:JsonProperty("experience_years")
    @field:Schema(description = "Стаж работы сотрудника в годах", example = "4.5", defaultValue = "0.0")
    val experienceYears: Double = 0.0,

    @field:JsonProperty("shift_id")
    @field:Schema(description = "UUID существующего графика смены (если указан, объект shift можно не передавать)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    val shiftId: UUID? = null,

    @field:Valid
    @field:Schema(description = "Параметры рабочего графика и смены (если не передан shift_id)")
    val shift: ShiftDto? = null,

    @field:Valid
    @field:JsonProperty("equipment_start")
    @field:Schema(description = "Начальный запас оборудования в рюкзаке монтажника", requiredMode = Schema.RequiredMode.REQUIRED)
    val equipmentStart: EquipmentDto = EquipmentDto(),

    @field:Valid
    @field:JsonProperty("equipment_capacity")
    @field:Schema(description = "Максимальная вместимость оборудования после пополнения (RESTOCK)", requiredMode = Schema.RequiredMode.REQUIRED)
    val equipmentCapacity: EquipmentDto = EquipmentDto()
)
