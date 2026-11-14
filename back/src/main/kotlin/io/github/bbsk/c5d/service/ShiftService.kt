package io.github.bbsk.c5d.service

import io.github.bbsk.c5d.exception.ConflictException
import io.github.bbsk.c5d.exception.NotFoundException
import io.github.bbsk.c5d.model.reference.dto.BreakWindowDto
import io.github.bbsk.c5d.model.reference.dto.request.CreateShiftRequest
import io.github.bbsk.c5d.model.reference.dto.response.ShiftResponse
import io.github.bbsk.c5d.model.reference.entity.Shift
import io.github.bbsk.c5d.repository.ShiftRepository
import io.github.bbsk.c5d.repository.StaffRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ShiftService(
    private val shiftRepository: ShiftRepository,
    private val staffRepository: StaffRepository
) {

    @Transactional(readOnly = true)
    fun getAll(): List<ShiftResponse> {
        return shiftRepository.findAll().map { it.toDto() }
    }

    @Transactional(readOnly = true)
    fun getById(id: UUID): ShiftResponse {
        val shift = shiftRepository.findById(id).orElseThrow {
            NotFoundException("SHIFT_NOT_FOUND", "График смены с id '$id' не найден")
        }
        return shift.toDto()
    }

    @Transactional
    fun create(request: CreateShiftRequest): ShiftResponse {
        val existing = shiftRepository.findFirstByStartTimeAndEndTimeAndBreakDurationMinAndBreakWindowFromAndBreakWindowTo(
            startTime = request.start,
            endTime = request.end,
            breakDurationMin = request.breakDurationMin,
            breakWindowFrom = request.breakWindow.from,
            breakWindowTo = request.breakWindow.to
        )

        val shift = existing ?: shiftRepository.save(
            Shift(
                id = UUID.randomUUID(),
                startTime = request.start,
                endTime = request.end,
                breakDurationMin = request.breakDurationMin,
                breakWindowFrom = request.breakWindow.from,
                breakWindowTo = request.breakWindow.to
            )
        )

        return shift.toDto()
    }

    @Transactional
    fun delete(id: UUID) {
        val shift = shiftRepository.findById(id).orElseThrow {
            NotFoundException("SHIFT_NOT_FOUND", "График смены с id '$id' не найден")
        }

        if (staffRepository.existsByShiftId(id)) {
            throw ConflictException(
                "SHIFT_IN_USE",
                "Невозможно удалить смену '$id', так как она назначена одному или нескольким сотрудникам"
            )
        }

        shiftRepository.delete(shift)
    }

    private fun Shift.toDto(): ShiftResponse {
        return ShiftResponse(
            id = this.id,
            start = this.startTime,
            end = this.endTime,
            breakDurationMin = this.breakDurationMin,
            breakWindow = BreakWindowDto(
                from = this.breakWindowFrom,
                to = this.breakWindowTo
            )
        )
    }
}
