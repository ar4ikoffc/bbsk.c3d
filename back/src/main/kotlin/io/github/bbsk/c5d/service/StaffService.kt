package io.github.bbsk.c5d.service

import io.github.bbsk.c5d.exception.BadRequestException
import io.github.bbsk.c5d.exception.ConflictException
import io.github.bbsk.c5d.exception.NotFoundException
import io.github.bbsk.c5d.model.reference.dto.BreakWindowDto
import io.github.bbsk.c5d.model.reference.dto.EquipmentDto
import io.github.bbsk.c5d.model.reference.dto.ShiftDto
import io.github.bbsk.c5d.model.reference.dto.request.CreateStaffRequest
import io.github.bbsk.c5d.model.reference.dto.request.UpdateStaffRequest
import io.github.bbsk.c5d.model.reference.dto.response.StaffResponse
import io.github.bbsk.c5d.model.reference.entity.Shift
import io.github.bbsk.c5d.model.reference.entity.Staff
import io.github.bbsk.c5d.repository.ServiceCenterRepository
import io.github.bbsk.c5d.repository.ShiftRepository
import io.github.bbsk.c5d.repository.StaffRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class StaffService(
    private val staffRepository: StaffRepository,
    private val serviceCenterRepository: ServiceCenterRepository,
    private val shiftRepository: ShiftRepository
) {

    @Transactional(readOnly = true)
    fun getAll(serviceCenterId: UUID? = null): List<StaffResponse> {
        val staffList = if (serviceCenterId != null) {
            staffRepository.findAllByServiceCenterId(serviceCenterId)
        } else {
            staffRepository.findAll()
        }
        return staffList.map { it.toDto() }
    }

    @Transactional(readOnly = true)
    fun getById(id: UUID): StaffResponse {
        val staff = staffRepository.findById(id).orElseThrow {
            NotFoundException("STAFF_NOT_FOUND", "Сотрудник с id '$id' не найден")
        }
        return staff.toDto()
    }

    @Transactional(readOnly = true)
    fun getByCode(code: String): StaffResponse {
        val staff = staffRepository.findByCode(code.trim())
            ?: throw NotFoundException("STAFF_NOT_FOUND", "Сотрудник с кодом '$code' не найден")
        return staff.toDto()
    }

    @Transactional
    fun create(request: CreateStaffRequest): StaffResponse {
        val trimmedCode = request.staffId.trim()
        if (staffRepository.existsByCode(trimmedCode)) {
            throw ConflictException("STAFF_ALREADY_EXISTS", "Сотрудник с кодом '$trimmedCode' уже существует")
        }

        val serviceCenter = serviceCenterRepository.findById(request.serviceCenterId).orElseThrow {
            NotFoundException(
                "SERVICE_CENTER_NOT_FOUND",
                "Сервисный центр с id '${request.serviceCenterId}' не найден в базе данных"
            )
        }

        val shift = when {
            request.shiftId != null -> shiftRepository.findById(request.shiftId).orElseThrow {
                NotFoundException("SHIFT_NOT_FOUND", "График смены с id '${request.shiftId}' не найден")
            }
            request.shift != null -> findOrCreateShift(request.shift)
            else -> throw BadRequestException("BAD_REQUEST", "Необходимо указать shift_id или объект shift")
        }

        val staff = Staff(
            id = UUID.randomUUID(),
            code = trimmedCode,
            displayName = request.displayName.trim(),
            serviceCenter = serviceCenter,
            shift = shift,
            heightPermit = request.heightPermit,
            experienceYears = request.experienceYears,
            equipmentStartOnt = request.equipmentStart.ont,
            equipmentStartRouter = request.equipmentStart.router,
            equipmentStartStb = request.equipmentStart.stb,
            equipmentCapacityOnt = request.equipmentCapacity.ont,
            equipmentCapacityRouter = request.equipmentCapacity.router,
            equipmentCapacityStb = request.equipmentCapacity.stb,
            profiles = request.profiles.toMutableSet(),
            technologies = request.technologies.toMutableSet()
        )

        val saved = staffRepository.save(staff)
        return saved.toDto()
    }

    @Transactional
    fun update(id: UUID, request: UpdateStaffRequest): StaffResponse {
        val staff = staffRepository.findById(id).orElseThrow {
            NotFoundException("STAFF_NOT_FOUND", "Сотрудник с id '$id' не найден")
        }

        if (request.displayName != null) {
            staff.displayName = request.displayName.trim()
        }

        if (request.serviceCenterId != null) {
            val sc = serviceCenterRepository.findById(request.serviceCenterId).orElseThrow {
                NotFoundException(
                    "SERVICE_CENTER_NOT_FOUND",
                    "Сервисный центр с id '${request.serviceCenterId}' не найден в базе данных"
                )
            }
            staff.serviceCenter = sc
        }

        if (request.shiftId != null) {
            val s = shiftRepository.findById(request.shiftId).orElseThrow {
                NotFoundException("SHIFT_NOT_FOUND", "График смены с id '${request.shiftId}' не найден")
            }
            staff.shift = s
        } else if (request.shift != null) {
            staff.shift = findOrCreateShift(request.shift)
        }

        if (request.heightPermit != null) {
            staff.heightPermit = request.heightPermit
        }

        if (request.experienceYears != null) {
            staff.experienceYears = request.experienceYears
        }

        if (request.profiles != null) {
            staff.profiles.clear()
            staff.profiles.addAll(request.profiles)
        }

        if (request.technologies != null) {
            staff.technologies.clear()
            staff.technologies.addAll(request.technologies)
        }

        if (request.equipmentStart != null) {
            staff.equipmentStartOnt = request.equipmentStart.ont
            staff.equipmentStartRouter = request.equipmentStart.router
            staff.equipmentStartStb = request.equipmentStart.stb
        }

        if (request.equipmentCapacity != null) {
            staff.equipmentCapacityOnt = request.equipmentCapacity.ont
            staff.equipmentCapacityRouter = request.equipmentCapacity.router
            staff.equipmentCapacityStb = request.equipmentCapacity.stb
        }

        val updated = staffRepository.save(staff)
        return updated.toDto()
    }

    @Transactional
    fun delete(id: UUID) {
        val staff = staffRepository.findById(id).orElseThrow {
            NotFoundException("STAFF_NOT_FOUND", "Сотрудник с id '$id' не найден")
        }
        staffRepository.delete(staff)
    }

    private fun findOrCreateShift(shiftDto: ShiftDto): Shift {
        val existing = shiftRepository.findFirstByStartTimeAndEndTimeAndBreakDurationMinAndBreakWindowFromAndBreakWindowTo(
            startTime = shiftDto.start,
            endTime = shiftDto.end,
            breakDurationMin = shiftDto.breakDurationMin,
            breakWindowFrom = shiftDto.breakWindow.from,
            breakWindowTo = shiftDto.breakWindow.to
        )

        return existing ?: shiftRepository.save(
            Shift(
                id = UUID.randomUUID(),
                startTime = shiftDto.start,
                endTime = shiftDto.end,
                breakDurationMin = shiftDto.breakDurationMin,
                breakWindowFrom = shiftDto.breakWindow.from,
                breakWindowTo = shiftDto.breakWindow.to
            )
        )
    }

    private fun Staff.toDto(): StaffResponse {
        return StaffResponse(
            id = this.id,
            staffId = this.code,
            displayName = this.displayName,
            serviceCenterId = this.serviceCenter.id,
            serviceCenterCode = this.serviceCenter.code,
            profiles = this.profiles.toSet(),
            technologies = this.technologies.toSet(),
            heightPermit = this.heightPermit,
            experienceYears = this.experienceYears,
            shift = ShiftDto(
                start = this.shift.startTime,
                end = this.shift.endTime,
                breakDurationMin = this.shift.breakDurationMin,
                breakWindow = BreakWindowDto(
                    from = this.shift.breakWindowFrom,
                    to = this.shift.breakWindowTo
                )
            ),
            equipmentStart = EquipmentDto(
                ont = this.equipmentStartOnt,
                router = this.equipmentStartRouter,
                stb = this.equipmentStartStb
            ),
            equipmentCapacity = EquipmentDto(
                ont = this.equipmentCapacityOnt,
                router = this.equipmentCapacityRouter,
                stb = this.equipmentCapacityStb
            )
        )
    }
}
