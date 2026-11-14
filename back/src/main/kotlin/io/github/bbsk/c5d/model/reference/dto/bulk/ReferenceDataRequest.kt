package io.github.bbsk.c5d.model.reference.dto.bulk

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.bbsk.c5d.model.reference.dto.request.CreateServiceCenterRequest
import io.github.bbsk.c5d.model.reference.dto.request.CreateWorkTypeRequest
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty

@Schema(description = "Пакетная загрузка всех справочных данных")
data class ReferenceDataRequest(
    @field:Valid
    @field:NotEmpty(message = "Список сервисных центров не может быть пустым")
    @field:JsonProperty("service_centers")
    @field:Schema(description = "Список сервисных центров", requiredMode = Schema.RequiredMode.REQUIRED)
    val serviceCenters: List<CreateServiceCenterRequest>,

    @field:Valid
    @field:NotEmpty(message = "Список сотрудников не может быть пустым")
    @field:Schema(description = "Список выездных сотрудников", requiredMode = Schema.RequiredMode.REQUIRED)
    val staff: List<ReferenceStaffDto>,

    @field:Valid
    @field:NotEmpty(message = "Список узлов доступа не может быть пустым")
    @field:JsonProperty("access_nodes")
    @field:Schema(description = "Список узлов доступа с обслуживаемыми домами", requiredMode = Schema.RequiredMode.REQUIRED)
    val accessNodes: List<ReferenceAccessNodeDto>,

    @field:Valid
    @field:NotEmpty(message = "Список типов работ не может быть пустым")
    @field:JsonProperty("work_types")
    @field:Schema(description = "Список типов выполняемых работ", requiredMode = Schema.RequiredMode.REQUIRED)
    val workTypes: List<CreateWorkTypeRequest>
)
