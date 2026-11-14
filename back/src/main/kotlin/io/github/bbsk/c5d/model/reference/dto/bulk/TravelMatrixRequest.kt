package io.github.bbsk.c5d.model.reference.dto.bulk

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

@Schema(description = "Запрос на загрузку единой матрицы времени в пути и расстояний")
data class TravelMatrixRequest(
    @field:JsonProperty("matrix_id")
    @field:NotBlank(message = "Идентификатор версии матрицы обязателен")
    @field:Schema(description = "Идентификатор версии матрицы", example = "ekb-2026-10-v1", requiredMode = Schema.RequiredMode.REQUIRED)
    val matrixId: String,

    @field:NotEmpty(message = "Список точек points не может быть пустым")
    @field:Schema(
        description = "Идентификаторы точек — все service_center_id и все building_id",
        example = "[\"SC-01\", \"SC-02\", \"B-00001\", \"B-00002\"]",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val points: List<String>,

    @field:JsonProperty("durations_sec")
    @field:NotEmpty(message = "Матрица времени durations_sec не может быть пустой")
    @field:Schema(description = "Квадратная матрица N×N времени в пути (секунды)", requiredMode = Schema.RequiredMode.REQUIRED)
    val durationsSec: List<List<Int>>,

    @field:JsonProperty("distances_m")
    @field:NotEmpty(message = "Матрица расстояний distances_m не может быть пустой")
    @field:Schema(description = "Квадратная матрица N×N расстояний (метры)", requiredMode = Schema.RequiredMode.REQUIRED)
    val distancesM: List<List<Int>>,

    @field:JsonProperty("hourly_factors")
    @field:Schema(description = "Коэффициенты загруженности дорог по часам суток")
    val hourlyFactors: List<HourlyFactorDto>? = null
)

@Schema(description = "Коэффициент загруженности дорог для конкретного часа")
data class HourlyFactorDto(
    @field:Schema(description = "Час суток (0..23)", example = "8", requiredMode = Schema.RequiredMode.REQUIRED)
    val hour: Int,

    @field:Schema(description = "Коэффициент замедления трафика", example = "1.35", requiredMode = Schema.RequiredMode.REQUIRED)
    val factor: Double
)
