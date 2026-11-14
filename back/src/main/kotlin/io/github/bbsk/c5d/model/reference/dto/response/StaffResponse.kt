package io.github.bbsk.c5d.model.reference.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.bbsk.c5d.model.reference.dto.EquipmentDto
import io.github.bbsk.c5d.model.reference.dto.ShiftDto
import io.github.bbsk.c5d.model.reference.enums.Profile
import io.github.bbsk.c5d.model.reference.enums.Technology
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "Информация о выездном сотруднике (бригаде)")
data class StaffResponse(
    @field:Schema(description = "Уникальный первичный ключ сотрудника (UUID)", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
    val id: UUID,

    @field:JsonProperty("staff_id")
    @field:Schema(description = "Код сотрудника (staff_id)", example = "ST-001")
    val staffId: String,

    @field:JsonProperty("display_name")
    @field:Schema(description = "Отображаемое имя сотрудника / бригады", example = "Иванов Иван Иванович")
    val displayName: String,

    @field:JsonProperty("service_center_id")
    @field:Schema(description = "UUID сервисного центра прикрепления", example = "b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e")
    val serviceCenterId: UUID,

    @field:JsonProperty("service_center_code")
    @field:Schema(description = "Код сервисного центра прикрепления", example = "SC-01")
    val serviceCenterCode: String,

    @field:Schema(description = "Квалификационные профили сотрудника", example = "[\"INSTALL\", \"LTP3\"]")
    val profiles: Set<Profile>,

    @field:Schema(description = "Поддерживаемые технологии подключения", example = "[\"PON\", \"FTTB\"]")
    val technologies: Set<Technology>,

    @field:JsonProperty("height_permit")
    @field:Schema(description = "Наличие допуска к работам на высоте (ВЛС)", example = "false")
    val heightPermit: Boolean,

    @field:JsonProperty("experience_years")
    @field:Schema(description = "Стаж работы сотрудника (лет)", example = "4.5")
    val experienceYears: Double,

    @field:Schema(description = "График рабочей смены")
    val shift: ShiftDto,

    @field:JsonProperty("equipment_start")
    @field:Schema(description = "Запас оборудования в начале смены")
    val equipmentStart: EquipmentDto,

    @field:JsonProperty("equipment_capacity")
    @field:Schema(description = "Максимальный запас оборудования после пополнения в СЦ (RESTOCK)")
    val equipmentCapacity: EquipmentDto
)
