package io.github.bbsk.c5d.controller

import io.github.bbsk.c5d.model.common.dto.ErrorResponse
import io.github.bbsk.c5d.model.reference.dto.request.CreateShiftRequest
import io.github.bbsk.c5d.model.reference.dto.request.CreateStaffRequest
import io.github.bbsk.c5d.model.reference.dto.request.UpdateStaffRequest
import io.github.bbsk.c5d.model.reference.dto.response.ShiftResponse
import io.github.bbsk.c5d.model.reference.dto.response.StaffResponse
import io.github.bbsk.c5d.service.ShiftService
import io.github.bbsk.c5d.service.StaffService
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

@Tag(name = "Сотрудники и смены", description = "Управление выездными сотрудниками, бригадами и графиками смен (CRUD)")
@RestController
@RequestMapping("/api/v1/staff")
class StaffController(
    private val staffService: StaffService,
    private val shiftService: ShiftService
) {

    @Operation(
        summary = "Получить список всех сотрудников",
        description = "Возвращает список всех выездных сотрудников с возможностью фильтрации по UUID сервисного центра."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Список сотрудников успешно получен",
                content = [Content(array = ArraySchema(schema = Schema(implementation = StaffResponse::class)))]
            )
        ]
    )
    @GetMapping
    fun getAll(
        @Parameter(description = "Фильтр по UUID сервисного центра", example = "b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e")
        @RequestParam(required = false, name = "service_center_id") serviceCenterId: UUID?
    ): ResponseEntity<List<StaffResponse>> {
        val list = staffService.getAll(serviceCenterId)
        return ResponseEntity.ok(list)
    }

    @Operation(
        summary = "Получить сотрудника по ID",
        description = "Возвращает полную информацию о конкретном сотруднике по его первичному ключу (UUID)."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Сотрудник найден",
                content = [Content(schema = Schema(implementation = StaffResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Сотрудник не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @GetMapping("/{id}")
    fun getById(
        @Parameter(description = "Уникальный идентификатор сотрудника (UUID)", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
        @PathVariable id: UUID
    ): ResponseEntity<StaffResponse> {
        val staff = staffService.getById(id)
        return ResponseEntity.ok(staff)
    }

    @Operation(
        summary = "Создать нового сотрудника",
        description = "Добавляет нового выездного сотрудника в базу данных."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Сотрудник успешно создан",
                content = [Content(schema = Schema(implementation = StaffResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Ошибка валидации переданных данных",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Указанный сервисный центр не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Сотрудник с таким кодом уже существует",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateStaffRequest
    ): ResponseEntity<StaffResponse> {
        val created = staffService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @Operation(
        summary = "Обновить данные сотрудника по ID",
        description = "Обновляет параметры существующего сотрудника по его первичному ключу (UUID)."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Сотрудник успешно обновлен",
                content = [Content(schema = Schema(implementation = StaffResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Ошибка валидации входных данных",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Сотрудник или сервисный центр не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @PutMapping("/{id}")
    fun update(
        @Parameter(description = "Уникальный идентификатор сотрудника (UUID)", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateStaffRequest
    ): ResponseEntity<StaffResponse> {
        val updated = staffService.update(id, request)
        return ResponseEntity.ok(updated)
    }

    @Operation(
        summary = "Удалить сотрудника по ID",
        description = "Удаляет сотрудника из системы по его первичному ключу (UUID)."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Сотрудник успешно удален"),
            ApiResponse(
                responseCode = "404",
                description = "Сотрудник не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @DeleteMapping("/{id}")
    fun delete(
        @Parameter(description = "Уникальный идентификатор сотрудника (UUID)", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
        @PathVariable id: UUID
    ): ResponseEntity<Unit> {
        staffService.delete(id)
        return ResponseEntity.noContent().build()
    }

    // ========================================================================
    // Графики смен (Shifts)
    // ========================================================================

    @Operation(
        summary = "Получить список всех графиков смен",
        description = "Возвращает перечень всех зарегистрированных в системе графиков рабочих смен."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Список графиков смен успешно получен",
                content = [Content(array = ArraySchema(schema = Schema(implementation = ShiftResponse::class)))]
            )
        ]
    )
    @GetMapping("/shifts")
    fun getAllShifts(): ResponseEntity<List<ShiftResponse>> {
        val shifts = shiftService.getAll()
        return ResponseEntity.ok(shifts)
    }

    @Operation(
        summary = "Получить график смены по ID",
        description = "Возвращает параметры графика смены по его первичному ключу (UUID)."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "График смены найден",
                content = [Content(schema = Schema(implementation = ShiftResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "График смены не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @GetMapping("/shifts/{id}")
    fun getShiftById(
        @Parameter(description = "Уникальный идентификатор смены (UUID)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @PathVariable id: UUID
    ): ResponseEntity<ShiftResponse> {
        val shift = shiftService.getById(id)
        return ResponseEntity.ok(shift)
    }

    @Operation(
        summary = "Создать новый график смены",
        description = "Добавляет новый график рабочей смены в базу данных."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "График смены успешно создан",
                content = [Content(schema = Schema(implementation = ShiftResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Ошибка валидации входных данных",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @PostMapping("/shifts")
    fun createShift(
        @Valid @RequestBody request: CreateShiftRequest
    ): ResponseEntity<ShiftResponse> {
        val created = shiftService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @Operation(
        summary = "Удалить график смены по ID",
        description = "Удаляет график смены из системы по его первичному ключу (UUID). Если к смене привязаны сотрудники, вернется ошибка 409 Conflict."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "График смены успешно удален"),
            ApiResponse(
                responseCode = "404",
                description = "График смены не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Невозможно удалить смену, так как она назначена сотрудникам",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @DeleteMapping("/shifts/{id}")
    fun deleteShift(
        @Parameter(description = "Уникальный идентификатор смены (UUID)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @PathVariable id: UUID
    ): ResponseEntity<Unit> {
        shiftService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
