package io.github.bbsk.c5d.service

import io.github.bbsk.c5d.exception.ConflictException
import io.github.bbsk.c5d.exception.NotFoundException
import io.github.bbsk.c5d.model.reference.dto.OpeningHoursDto
import io.github.bbsk.c5d.model.reference.dto.request.CreateServiceCenterRequest
import io.github.bbsk.c5d.model.reference.dto.request.UpdateServiceCenterRequest
import io.github.bbsk.c5d.model.reference.dto.response.ServiceCenterResponse
import io.github.bbsk.c5d.model.reference.entity.ServiceCenter
import io.github.bbsk.c5d.repository.BuildingRepository
import io.github.bbsk.c5d.repository.ServiceCenterRepository
import io.github.bbsk.c5d.repository.StaffRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ServiceCenterService(
    private val serviceCenterRepository: ServiceCenterRepository,
    private val staffRepository: StaffRepository
) {

    @Transactional(readOnly = true)
    fun getAll(): List<ServiceCenterResponse> {
        return serviceCenterRepository.findAll().map { it.toDto() }
    }

    @Transactional(readOnly = true)
    fun getById(id: UUID): ServiceCenterResponse {
        val sc = serviceCenterRepository.findById(id).orElseThrow {
            NotFoundException("SERVICE_CENTER_NOT_FOUND", "Сервисный центр с id '$id' не найден")
        }
        return sc.toDto()
    }

    @Transactional(readOnly = true)
    fun getByCode(code: String): ServiceCenterResponse {
        val sc = serviceCenterRepository.findByCode(code.trim())
            ?: throw NotFoundException("SERVICE_CENTER_NOT_FOUND", "Сервисный центр с кодом '$code' не найден")
        return sc.toDto()
    }

    @Transactional
    fun create(request: CreateServiceCenterRequest): ServiceCenterResponse {
        val trimmedCode = request.serviceCenterId.trim()
        if (serviceCenterRepository.existsByCode(trimmedCode)) {
            throw ConflictException(
                "SERVICE_CENTER_ALREADY_EXISTS",
                "Сервисный центр с кодом '$trimmedCode' уже существует"
            )
        }

        val sc = ServiceCenter(
            id = UUID.randomUUID(),
            code = trimmedCode,
            name = request.name.trim(),
            address = request.address.trim(),
            lat = request.lat,
            lon = request.lon,
            openTime = request.openingHours.open,
            closeTime = request.openingHours.close,
            restockDurationMin = request.restockDurationMin
        )

        val saved = serviceCenterRepository.save(sc)
        return saved.toDto()
    }

    @Transactional
    fun update(id: UUID, request: UpdateServiceCenterRequest): ServiceCenterResponse {
        val sc = serviceCenterRepository.findById(id).orElseThrow {
            NotFoundException("SERVICE_CENTER_NOT_FOUND", "Сервисный центр с id '$id' не найден")
        }

        if (request.name != null) {
            sc.name = request.name.trim()
        }
        if (request.address != null) {
            sc.address = request.address.trim()
        }
        if (request.lat != null) {
            sc.lat = request.lat
        }
        if (request.lon != null) {
            sc.lon = request.lon
        }
        if (request.openingHours != null) {
            sc.openTime = request.openingHours.open
            sc.closeTime = request.openingHours.close
        }
        if (request.restockDurationMin != null) {
            sc.restockDurationMin = request.restockDurationMin
        }

        val updated = serviceCenterRepository.save(sc)
        return updated.toDto()
    }

    @Transactional
    fun delete(id: UUID) {
        val sc = serviceCenterRepository.findById(id).orElseThrow {
            NotFoundException("SERVICE_CENTER_NOT_FOUND", "Сервисный центр с id '$id' не найден")
        }

        if (staffRepository.existsByServiceCenterId(id)) {
            throw ConflictException(
                "SERVICE_CENTER_IN_USE",
                "Невозможно удалить сервисный центр '${sc.code}', так как к нему привязаны сотрудники"
            )
        }

        serviceCenterRepository.delete(sc)
    }

    private fun ServiceCenter.toDto(): ServiceCenterResponse {
        return ServiceCenterResponse(
            id = this.id,
            serviceCenterId = this.code,
            name = this.name,
            address = this.address,
            lat = this.lat,
            lon = this.lon,
            openingHours = OpeningHoursDto(
                open = this.openTime,
                close = this.closeTime
            ),
            restockDurationMin = this.restockDurationMin
        )
    }
}
