package io.github.bbsk.c5d.service

import io.github.bbsk.c5d.exception.ConflictException
import io.github.bbsk.c5d.exception.NotFoundException
import io.github.bbsk.c5d.model.reference.dto.request.CreateWorkTypeRequest
import io.github.bbsk.c5d.model.reference.dto.request.UpdateWorkTypeRequest
import io.github.bbsk.c5d.model.reference.dto.response.WorkTypeResponse
import io.github.bbsk.c5d.model.reference.entity.WorkType
import io.github.bbsk.c5d.model.reference.enums.WorkTypeCode
import io.github.bbsk.c5d.repository.WorkTypeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class WorkTypeService(
    private val workTypeRepository: WorkTypeRepository
) {

    @Transactional(readOnly = true)
    fun getAll(): List<WorkTypeResponse> {
        return workTypeRepository.findAll().map { it.toDto() }
    }

    @Transactional(readOnly = true)
    fun getById(id: UUID): WorkTypeResponse {
        val workType = workTypeRepository.findById(id).orElseThrow {
            NotFoundException("WORK_TYPE_NOT_FOUND", "Тип работ с id '$id' не найден")
        }
        return workType.toDto()
    }

    @Transactional(readOnly = true)
    fun getByCode(code: WorkTypeCode): WorkTypeResponse {
        val workType = workTypeRepository.findByCode(code)
            ?: throw NotFoundException("WORK_TYPE_NOT_FOUND", "Тип работ с кодом '$code' не найден")
        return workType.toDto()
    }

    @Transactional
    fun create(request: CreateWorkTypeRequest): WorkTypeResponse {
        if (workTypeRepository.existsByCode(request.code)) {
            throw ConflictException(
                "WORK_TYPE_ALREADY_EXISTS",
                "Тип работ с кодом '${request.code}' уже существует"
            )
        }

        val workType = WorkType(
            id = UUID.randomUUID(),
            code = request.code,
            name = request.name.trim(),
            durationMin = request.durationMin,
            maxStartDelayMin = request.maxStartDelayMin,
            deadlineKind = request.deadlineKind,
            deadlineValue = request.deadlineValue,
            repeatDeadlineValue = request.repeatDeadlineValue,
            pgpRelevant = request.pgpRelevant,
            profiles = request.profiles.toMutableSet()
        )

        val saved = workTypeRepository.save(workType)
        return saved.toDto()
    }

    @Transactional
    fun update(id: UUID, request: UpdateWorkTypeRequest): WorkTypeResponse {
        val workType = workTypeRepository.findById(id).orElseThrow {
            NotFoundException("WORK_TYPE_NOT_FOUND", "Тип работ с id '$id' не найден")
        }

        if (request.code != null && request.code != workType.code) {
            if (workTypeRepository.existsByCode(request.code)) {
                throw ConflictException(
                    "WORK_TYPE_ALREADY_EXISTS",
                    "Тип работ с кодом '${request.code}' уже существует"
                )
            }
            workType.code = request.code
        }

        if (request.name != null) {
            workType.name = request.name.trim()
        }
        if (request.durationMin != null) {
            workType.durationMin = request.durationMin
        }
        if (request.maxStartDelayMin != null) {
            workType.maxStartDelayMin = request.maxStartDelayMin
        }
        if (request.deadlineKind != null) {
            workType.deadlineKind = request.deadlineKind
        }
        if (request.deadlineValue != null) {
            workType.deadlineValue = request.deadlineValue
        }
        if (request.repeatDeadlineValue != null) {
            workType.repeatDeadlineValue = request.repeatDeadlineValue
        }
        if (request.pgpRelevant != null) {
            workType.pgpRelevant = request.pgpRelevant
        }
        if (request.profiles != null) {
            workType.profiles.clear()
            workType.profiles.addAll(request.profiles)
        }

        val updated = workTypeRepository.save(workType)
        return updated.toDto()
    }

    @Transactional
    fun delete(id: UUID) {
        val workType = workTypeRepository.findById(id).orElseThrow {
            NotFoundException("WORK_TYPE_NOT_FOUND", "Тип работ с id '$id' не найден")
        }
        workTypeRepository.delete(workType)
    }

    private fun WorkType.toDto(): WorkTypeResponse {
        return WorkTypeResponse(
            id = id,
            code = code,
            name = name,
            durationMin = durationMin,
            maxStartDelayMin = maxStartDelayMin,
            deadlineKind = deadlineKind,
            deadlineValue = deadlineValue,
            repeatDeadlineValue = repeatDeadlineValue,
            pgpRelevant = pgpRelevant,
            profiles = profiles.toSet()
        )
    }
}
