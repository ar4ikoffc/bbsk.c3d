package io.github.bbsk.c5d.service

import io.github.bbsk.c5d.exception.ConflictException
import io.github.bbsk.c5d.exception.NotFoundException
import io.github.bbsk.c5d.model.reference.dto.request.CreateBuildingRequest
import io.github.bbsk.c5d.model.reference.dto.request.UpdateBuildingRequest
import io.github.bbsk.c5d.model.reference.dto.response.BuildingResponse
import io.github.bbsk.c5d.model.reference.entity.Building
import io.github.bbsk.c5d.model.reference.enums.BuildingType
import io.github.bbsk.c5d.repository.AccessNodeRepository
import io.github.bbsk.c5d.repository.BuildingRepository
import io.github.bbsk.c5d.repository.ServiceCenterRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class BuildingService(
    private val buildingRepository: BuildingRepository,
    private val accessNodeRepository: AccessNodeRepository,
    private val serviceCenterRepository: ServiceCenterRepository
) {

    @Transactional(readOnly = true)
    fun getAll(
        buildingType: BuildingType? = null,
        accessNodeId: UUID? = null,
        zoneServiceCenterId: UUID? = null
    ): List<BuildingResponse> {
        val list = when {
            buildingType != null -> buildingRepository.findAllByBuildingType(buildingType)
            accessNodeId != null -> buildingRepository.findAllByAccessNodeId(accessNodeId)
            zoneServiceCenterId != null -> buildingRepository.findAllByZoneServiceCenterId(zoneServiceCenterId)
            else -> buildingRepository.findAll()
        }
        return list.map { it.toDto() }
    }

    @Transactional(readOnly = true)
    fun getById(id: UUID): BuildingResponse {
        val building = buildingRepository.findById(id).orElseThrow {
            NotFoundException("BUILDING_NOT_FOUND", "Здание с id '$id' не найдено")
        }
        return building.toDto()
    }

    @Transactional(readOnly = true)
    fun getByCode(code: String): BuildingResponse {
        val building = buildingRepository.findByCode(code.trim())
            ?: throw NotFoundException("BUILDING_NOT_FOUND", "Здание с кодом '$code' не найдено")
        return building.toDto()
    }

    @Transactional
    fun create(request: CreateBuildingRequest): BuildingResponse {
        val trimmedCode = request.buildingId.trim()
        if (buildingRepository.existsByCode(trimmedCode)) {
            throw ConflictException(
                "BUILDING_ALREADY_EXISTS",
                "Здание с кодом '$trimmedCode' уже существует"
            )
        }

        val accessNode = accessNodeRepository.findById(request.accessNodeId).orElseThrow {
            NotFoundException(
                "ACCESS_NODE_NOT_FOUND",
                "Узел доступа с id '${request.accessNodeId}' не найден"
            )
        }

        val zoneServiceCenter = if (request.zoneServiceCenterId != null) {
            serviceCenterRepository.findById(request.zoneServiceCenterId).orElseThrow {
                NotFoundException(
                    "SERVICE_CENTER_NOT_FOUND",
                    "Сервисный центр с id '${request.zoneServiceCenterId}' не найден"
                )
            }
        } else null

        val building = Building(
            id = UUID.randomUUID(),
            code = trimmedCode,
            accessNode = accessNode,
            zoneServiceCenter = zoneServiceCenter,
            address = request.address.trim(),
            lat = request.lat,
            lon = request.lon,
            buildingType = request.buildingType,
            overheadLine = request.overheadLine
        )

        val saved = buildingRepository.save(building)
        return saved.toDto()
    }

    @Transactional
    fun update(id: UUID, request: UpdateBuildingRequest): BuildingResponse {
        val building = buildingRepository.findById(id).orElseThrow {
            NotFoundException("BUILDING_NOT_FOUND", "Здание с id '$id' не найдено")
        }

        if (request.buildingId != null) {
            val trimmedCode = request.buildingId.trim()
            if (trimmedCode != building.code && buildingRepository.existsByCode(trimmedCode)) {
                throw ConflictException(
                    "BUILDING_ALREADY_EXISTS",
                    "Здание с кодом '$trimmedCode' уже существует"
                )
            }
            building.code = trimmedCode
        }

        if (request.accessNodeId != null) {
            val accessNode = accessNodeRepository.findById(request.accessNodeId).orElseThrow {
                NotFoundException(
                    "ACCESS_NODE_NOT_FOUND",
                    "Узел доступа с id '${request.accessNodeId}' не найден"
                )
            }
            building.accessNode = accessNode
        }

        if (request.zoneServiceCenterId != null) {
            val sc = serviceCenterRepository.findById(request.zoneServiceCenterId).orElseThrow {
                NotFoundException(
                    "SERVICE_CENTER_NOT_FOUND",
                    "Сервисный центр с id '${request.zoneServiceCenterId}' не найден"
                )
            }
            building.zoneServiceCenter = sc
        }

        if (request.address != null) {
            building.address = request.address.trim()
        }
        if (request.lat != null) {
            building.lat = request.lat
        }
        if (request.lon != null) {
            building.lon = request.lon
        }
        if (request.buildingType != null) {
            building.buildingType = request.buildingType
        }
        if (request.overheadLine != null) {
            building.overheadLine = request.overheadLine
        }

        val updated = buildingRepository.save(building)
        return updated.toDto()
    }

    @Transactional
    fun delete(id: UUID) {
        val building = buildingRepository.findById(id).orElseThrow {
            NotFoundException("BUILDING_NOT_FOUND", "Здание с id '$id' не найдено")
        }
        buildingRepository.delete(building)
    }

    private fun Building.toDto(): BuildingResponse {
        return BuildingResponse(
            id = id,
            buildingId = code,
            accessNodeId = accessNode.id,
            accessNodeCode = accessNode.code,
            zoneServiceCenterId = zoneServiceCenter?.id,
            zoneServiceCenterCode = zoneServiceCenter?.code,
            address = address,
            lat = lat,
            lon = lon,
            buildingType = buildingType,
            overheadLine = overheadLine
        )
    }
}
