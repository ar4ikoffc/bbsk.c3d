package io.github.bbsk.c5d.model.reference.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.bbsk.c5d.model.reference.dto.EquipmentDto
import io.github.bbsk.c5d.model.reference.dto.ShiftDto
import io.github.bbsk.c5d.model.reference.enums.Profile
import io.github.bbsk.c5d.model.reference.enums.Technology
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import java.util.UUID

@Schema(description = "Запрос на обновление данных выездного сотрудника (бригады)")
data class UpdateStaffRequest(
    @field:JsonProperty("display_name")
    @field:Schema(description = "Новое отображаемое имя сотрудника", example = "Петров Петр Петрович")
    val displayName: String? = null,

    @field:JsonProperty("service_center_id")
    @field:Schema(description = "Новый сервисный центр прикрепления (UUID)", example = "b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e")
    val serviceCenterId: UUID? = null,

    @field:Schema(description = "Обновленный список рабочих профилей", example = "[\"LTP3\"]")
    val profiles: Set<Profile>? = null,

    @field:Schema(description = "Обновленный список технологий", example = "[\"PON\", \"FTTB\", \"XDSL\"]")
    val technologies: Set<Technology>? = null,

    @field:JsonProperty("height_permit")
    @field:Schema(description = "Наличие допуска к работам на высоте", example = "true")
    val heightPermit: Boolean? = null,

    @field:JsonProperty("experience_years")
    @field:Schema(description = "Стаж работы (лет)", example = "5.0")
    val experienceYears: Double? = null,

    @field:JsonProperty("shift_id")
    @field:Schema(description = "UUID нового графика смены", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    val shiftId: UUID? = null,

    @field:Valid
    @field:Schema(description = "Обновленный график смены")
    val shift: ShiftDto? = null,

    @field:Valid
    @field:JsonProperty("equipment_start")
    @field:Schema(description = "Начальный запас оборудования")
    val equipmentStart: EquipmentDto? = null,

    @field:Valid
    @field:JsonProperty("equipment_capacity")
    @field:Schema(description = "Максимальная вместимость оборудования")
    val equipmentCapacity: EquipmentDto? = null
)
