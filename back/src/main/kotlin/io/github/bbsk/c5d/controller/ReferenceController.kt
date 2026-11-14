package io.github.bbsk.c5d.controller

import io.github.bbsk.c5d.model.common.dto.ErrorResponse
import io.github.bbsk.c5d.model.reference.dto.bulk.LoadResultResponse
import io.github.bbsk.c5d.model.reference.dto.bulk.ReferenceDataRequest
import io.github.bbsk.c5d.model.reference.dto.bulk.TravelMatrixRequest
import io.github.bbsk.c5d.service.ReferenceService
import io.github.bbsk.c5d.service.TravelMatrixService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Справочные данные", description = "Пакетная загрузка справочников и матрицы времени в пути (один раз за тестовый прогон)")
@RestController
@RequestMapping("/api/v1/reference")
class ReferenceController(
    private val referenceService: ReferenceService,
    private val travelMatrixService: TravelMatrixService
) {

    @Operation(
        summary = "Загрузить справочники",
        description = "Полностью заменяет справочные данные: СЦ, сотрудники со сменами, узлы доступа с домами, типы работ. Все загруженные ранее дни, планы и события удаляются."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Справочники успешно загружены",
                content = [Content(schema = Schema(implementation = LoadResultResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Ошибка валидации входных данных",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Ошибка целостности данных",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @PutMapping
    fun putReference(
        @Valid @RequestBody request: ReferenceDataRequest
    ): ResponseEntity<LoadResultResponse> {
        val result = referenceService.loadReferenceData(request)
        return ResponseEntity.ok(result)
    }

    @Operation(
        summary = "Загрузить единую матрицу времени в пути",
        description = "Матрица покрывает все СЦ и все дома из access_nodes[].buildings[]. Повторный вызов заменяет матрицу."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Матрица успешно загружена",
                content = [Content(schema = Schema(implementation = LoadResultResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Ошибка валидации входных данных или несовпадение размерности",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @PutMapping("/travel-matrix")
    fun putTravelMatrix(
        @Valid @RequestBody request: TravelMatrixRequest
    ): ResponseEntity<LoadResultResponse> {
        val pointsCount = travelMatrixService.save(request)
        val result = LoadResultResponse(
            status = "loaded",
            counts = mapOf("points" to pointsCount)
        )
        return ResponseEntity.ok(result)
    }
}
