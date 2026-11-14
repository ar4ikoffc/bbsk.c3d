package io.github.bbsk.c5d.controller

import io.github.bbsk.c5d.model.common.dto.ErrorResponse
import io.github.bbsk.c5d.model.reference.dto.request.CreateWorkTypeRequest
import io.github.bbsk.c5d.model.reference.dto.request.UpdateWorkTypeRequest
import io.github.bbsk.c5d.model.reference.dto.response.WorkTypeResponse
import io.github.bbsk.c5d.service.WorkTypeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@Tag(name = "Типы работ", description = "Справочник и управление типами выполняемых работ (CRUD)")
@RestController
@RequestMapping("/api/v1/work-types")
class WorkTypeController(
    private val workTypeService: WorkTypeService
) {

    @Operation(
        summary = "Получить список всех типов работ",
        description = "Возвращает справочник всех типов работ со всеми нормативными параметрами и профилями."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Список типов работ успешно получен",
                content = [Content(array = ArraySchema(schema = Schema(implementation = WorkTypeResponse::class)))]
            )
        ]
    )
    @GetMapping
    fun getAll(): ResponseEntity<List<WorkTypeResponse>> {
        val list = workTypeService.getAll()
        return ResponseEntity.ok(list)
    }

    @Operation(
        summary = "Получить тип работ по ID",
        description = "Возвращает информацию о конкретном типе работ по его первичному ключу (UUID)."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Тип работ найден",
                content = [Content(schema = Schema(implementation = WorkTypeResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Тип работ не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @GetMapping("/{id}")
    fun getById(
        @Parameter(description = "Уникальный идентификатор типа работ (UUID)", example = "10000000-0000-0000-0000-000000000001")
        @PathVariable id: UUID
    ): ResponseEntity<WorkTypeResponse> {
        val workType = workTypeService.getById(id)
        return ResponseEntity.ok(workType)
    }

    @Operation(
        summary = "Создать новый тип работ",
        description = "Добавляет новый тип работ в справочник."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Тип работ успешно создан",
                content = [Content(schema = Schema(implementation = WorkTypeResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Ошибка валидации входных данных",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Тип работ с таким кодом уже существует",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateWorkTypeRequest
    ): ResponseEntity<WorkTypeResponse> {
        val created = workTypeService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @Operation(
        summary = "Обновить тип работ по ID",
        description = "Обновляет параметры типа работ по его первичному ключу (UUID)."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Тип работ успешно обновлен",
                content = [Content(schema = Schema(implementation = WorkTypeResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Ошибка валидации входных данных",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Тип работ не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Конфликт: тип работ с указанным кодом уже существует",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @PutMapping("/{id}")
    fun update(
        @Parameter(description = "Уникальный идентификатор типа работ (UUID)", example = "10000000-0000-0000-0000-000000000001")
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateWorkTypeRequest
    ): ResponseEntity<WorkTypeResponse> {
        val updated = workTypeService.update(id, request)
        return ResponseEntity.ok(updated)
    }

    @Operation(
        summary = "Удалить тип работ по ID",
        description = "Удаляет тип работ из справочника по его первичному ключу (UUID)."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Тип работ успешно удален"),
            ApiResponse(
                responseCode = "404",
                description = "Тип работ не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @DeleteMapping("/{id}")
    fun delete(
        @Parameter(description = "Уникальный идентификатор типа работ (UUID)", example = "10000000-0000-0000-0000-000000000001")
        @PathVariable id: UUID
    ): ResponseEntity<Unit> {
        workTypeService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
