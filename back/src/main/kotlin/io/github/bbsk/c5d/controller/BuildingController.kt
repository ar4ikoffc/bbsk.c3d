package io.github.bbsk.c5d.controller

import io.github.bbsk.c5d.model.common.dto.ErrorResponse
import io.github.bbsk.c5d.model.reference.dto.request.*
import io.github.bbsk.c5d.model.reference.dto.response.AccessNodeResponse
import io.github.bbsk.c5d.model.reference.dto.response.BuildingResponse
import io.github.bbsk.c5d.model.reference.dto.response.ServiceCenterResponse
import io.github.bbsk.c5d.model.reference.enums.BuildingType
import io.github.bbsk.c5d.model.reference.enums.Technology
import io.github.bbsk.c5d.service.AccessNodeService
import io.github.bbsk.c5d.service.BuildingService
import io.github.bbsk.c5d.service.ServiceCenterService
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

@Tag(name = "Инфраструктура и здания", description = "Управление объектами инфраструктуры: здания, узлы доступа связи и сервисные центры (CRUD)")
@RestController
@RequestMapping("/api/v1/buildings")
class BuildingController(
    private val buildingService: BuildingService,
    private val accessNodeService: AccessNodeService,
    private val serviceCenterService: ServiceCenterService
) {

    // ==========================================
    // ЗДАНИЯ (BUILDINGS)
    // ==========================================

    @Operation(
        summary = "Получить список зданий",
        description = "Возвращает список зданий с возможностью фильтрации по типу здания, узлу доступа или СЦ."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Список зданий успешно получен",
                content = [Content(array = ArraySchema(schema = Schema(implementation = BuildingResponse::class)))]
            )
        ]
    )
    @GetMapping
    fun getAllBuildings(
        @Parameter(description = "Фильтр по типу здания (MKD / PRIVATE)", example = "MKD")
        @RequestParam(required = false, name = "building_type") buildingType: BuildingType?,

        @Parameter(description = "Фильтр по UUID узла доступа связи", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @RequestParam(required = false, name = "access_node_id") accessNodeId: UUID?,

        @Parameter(description = "Фильтр по UUID сервисного центра зоны", example = "b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e")
        @RequestParam(required = false, name = "service_center_id") serviceCenterId: UUID?
    ): ResponseEntity<List<BuildingResponse>> {
        val list = buildingService.getAll(buildingType, accessNodeId, serviceCenterId)
        return ResponseEntity.ok(list)
    }

    @Operation(
        summary = "Получить здание по ID",
        description = "Возвращает полную информацию о здании по его первичному ключу (UUID)."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Здание найдено",
                content = [Content(schema = Schema(implementation = BuildingResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Здание не найдено",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @GetMapping("/{id}")
    fun getBuildingById(
        @Parameter(description = "Уникальный идентификатор здания (UUID)", example = "4ba85f64-5717-4562-b3fc-2c963f66afa7")
        @PathVariable id: UUID
    ): ResponseEntity<BuildingResponse> {
        val building = buildingService.getById(id)
        return ResponseEntity.ok(building)
    }

    @Operation(
        summary = "Создать новое здание",
        description = "Добавляет новое обслуживаемое здание с привязкой к узлу доступа и сервисному центру."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Здание успешно создано",
                content = [Content(schema = Schema(implementation = BuildingResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Ошибка валидации входных данных",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Указанный узел доступа или сервисный центр не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Здание с таким кодом уже существует",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @PostMapping
    fun createBuilding(
        @Valid @RequestBody request: CreateBuildingRequest
    ): ResponseEntity<BuildingResponse> {
        val created = buildingService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @Operation(
        summary = "Обновить данные здания по ID",
        description = "Обновляет параметры здания по его первичному ключу (UUID)."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Здание успешно обновлено",
                content = [Content(schema = Schema(implementation = BuildingResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Ошибка валидации входных данных",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Здание, узел доступа или сервисный центр не найдены",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Конфликт уникальности кода здания",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @PutMapping("/{id}")
    fun updateBuilding(
        @Parameter(description = "Уникальный идентификатор здания (UUID)", example = "4ba85f64-5717-4562-b3fc-2c963f66afa7")
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateBuildingRequest
    ): ResponseEntity<BuildingResponse> {
        val updated = buildingService.update(id, request)
        return ResponseEntity.ok(updated)
    }

    @Operation(
        summary = "Удалить здание по ID",
        description = "Удаляет здание по его первичному ключу (UUID)."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Здание успешно удалено"),
            ApiResponse(
                responseCode = "404",
                description = "Здание не найдено",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @DeleteMapping("/{id}")
    fun deleteBuilding(
        @Parameter(description = "Уникальный идентификатор здания (UUID)", example = "4ba85f64-5717-4562-b3fc-2c963f66afa7")
        @PathVariable id: UUID
    ): ResponseEntity<Unit> {
        buildingService.delete(id)
        return ResponseEntity.noContent().build()
    }

    // ==========================================
    // УЗЛЫ ДОСТУПА (ACCESS NODES)
    // ==========================================

    @Operation(
        summary = "Получить список узлов доступа",
        description = "Возвращает список всех зарегистрированных узлов доступа связи с фильтрацией по технологии."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Список узлов доступа успешно получен",
                content = [Content(array = ArraySchema(schema = Schema(implementation = AccessNodeResponse::class)))]
            )
        ]
    )
    @GetMapping("/access-nodes")
    fun getAllAccessNodes(
        @Parameter(description = "Фильтр по технологии связи (PON, FTTB, XDSL, DOCSIS)", example = "PON")
        @RequestParam(required = false) technology: Technology?
    ): ResponseEntity<List<AccessNodeResponse>> {
        val list = accessNodeService.getAll(technology)
        return ResponseEntity.ok(list)
    }

    @Operation(
        summary = "Получить узел доступа по ID",
        description = "Возвращает информацию об узле доступа по его первичному ключу (UUID)."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Узел доступа найден",
                content = [Content(schema = Schema(implementation = AccessNodeResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Узел доступа не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @GetMapping("/access-nodes/{id}")
    fun getAccessNodeById(
        @Parameter(description = "Уникальный идентификатор узла доступа (UUID)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @PathVariable id: UUID
    ): ResponseEntity<AccessNodeResponse> {
        val node = accessNodeService.getById(id)
        return ResponseEntity.ok(node)
    }

    @Operation(
        summary = "Создать новый узел доступа",
        description = "Добавляет новый узел доступа связи в справочник."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Узел доступа успешно создан",
                content = [Content(schema = Schema(implementation = AccessNodeResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Ошибка валидации входных данных",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Узел доступа с таким кодом уже существует",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @PostMapping("/access-nodes")
    fun createAccessNode(
        @Valid @RequestBody request: CreateAccessNodeRequest
    ): ResponseEntity<AccessNodeResponse> {
        val created = accessNodeService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @Operation(
        summary = "Обновить данные узла доступа по ID",
        description = "Обновляет параметры узла доступа по его первичному ключу (UUID)."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Узел доступа успешно обновлен",
                content = [Content(schema = Schema(implementation = AccessNodeResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Ошибка валидации входных данных",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Узел доступа не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Конфликт уникальности кода узла доступа",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @PutMapping("/access-nodes/{id}")
    fun updateAccessNode(
        @Parameter(description = "Уникальный идентификатор узла доступа (UUID)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateAccessNodeRequest
    ): ResponseEntity<AccessNodeResponse> {
        val updated = accessNodeService.update(id, request)
        return ResponseEntity.ok(updated)
    }

    @Operation(
        summary = "Удалить узел доступа по ID",
        description = "Удаляет узел доступа по его первичному ключу (UUID). Запрещено удалять, если к узлу привязаны здания."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Узел доступа успешно удален"),
            ApiResponse(
                responseCode = "404",
                description = "Узел доступа не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Невозможно удалить узел доступа, так как к нему привязаны здания",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @DeleteMapping("/access-nodes/{id}")
    fun deleteAccessNode(
        @Parameter(description = "Уникальный идентификатор узла доступа (UUID)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @PathVariable id: UUID
    ): ResponseEntity<Unit> {
        accessNodeService.delete(id)
        return ResponseEntity.noContent().build()
    }

    // ==========================================
    // СЕРВИСНЫЕ ЦЕНТРЫ (SERVICE CENTERS)
    // ==========================================

    @Operation(
        summary = "Получить список сервисных центров",
        description = "Возвращает список всех сервисных центров компании."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Список СЦ успешно получен",
                content = [Content(array = ArraySchema(schema = Schema(implementation = ServiceCenterResponse::class)))]
            )
        ]
    )
    @GetMapping("/service-centers")
    fun getAllServiceCenters(): ResponseEntity<List<ServiceCenterResponse>> {
        val list = serviceCenterService.getAll()
        return ResponseEntity.ok(list)
    }

    @Operation(
        summary = "Получить сервисный центр по ID",
        description = "Возвращает информацию о конкретном сервисном центре по его первичному ключу (UUID)."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Сервисный центр найден",
                content = [Content(schema = Schema(implementation = ServiceCenterResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Сервисный центр не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @GetMapping("/service-centers/{id}")
    fun getServiceCenterById(
        @Parameter(description = "Уникальный идентификатор СЦ (UUID)", example = "b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e")
        @PathVariable id: UUID
    ): ResponseEntity<ServiceCenterResponse> {
        val sc = serviceCenterService.getById(id)
        return ResponseEntity.ok(sc)
    }

    @Operation(
        summary = "Создать новый сервисный центр",
        description = "Добавляет новый сервисный центр в базу данных."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Сервисный центр успешно создан",
                content = [Content(schema = Schema(implementation = ServiceCenterResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Ошибка валидации входных данных",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Сервисный центр с таким кодом уже существует",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @PostMapping("/service-centers")
    fun createServiceCenter(
        @Valid @RequestBody request: CreateServiceCenterRequest
    ): ResponseEntity<ServiceCenterResponse> {
        val created = serviceCenterService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @Operation(
        summary = "Обновить данные сервисного центра по ID",
        description = "Обновляет параметры сервисного центра по его первичному ключу (UUID)."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Сервисный центр успешно обновлен",
                content = [Content(schema = Schema(implementation = ServiceCenterResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Ошибка валидации входных данных",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Сервисный центр не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @PutMapping("/service-centers/{id}")
    fun updateServiceCenter(
        @Parameter(description = "Уникальный идентификатор СЦ (UUID)", example = "b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e")
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateServiceCenterRequest
    ): ResponseEntity<ServiceCenterResponse> {
        val updated = serviceCenterService.update(id, request)
        return ResponseEntity.ok(updated)
    }

    @Operation(
        summary = "Удалить сервисный центр по ID",
        description = "Удаляет сервисный центр из базы данных по его первичному ключу (UUID)."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Сервисный центр успешно удален"),
            ApiResponse(
                responseCode = "404",
                description = "Сервисный центр не найден",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @DeleteMapping("/service-centers/{id}")
    fun deleteServiceCenter(
        @Parameter(description = "Уникальный идентификатор СЦ (UUID)", example = "b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e")
        @PathVariable id: UUID
    ): ResponseEntity<Unit> {
        serviceCenterService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
