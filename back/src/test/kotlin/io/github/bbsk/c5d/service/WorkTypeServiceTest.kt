package io.github.bbsk.c5d.service

import io.github.bbsk.c5d.exception.ConflictException
import io.github.bbsk.c5d.exception.NotFoundException
import io.github.bbsk.c5d.model.reference.dto.request.CreateWorkTypeRequest
import io.github.bbsk.c5d.model.reference.dto.request.UpdateWorkTypeRequest
import io.github.bbsk.c5d.model.reference.entity.WorkType
import io.github.bbsk.c5d.model.reference.enums.DeadlineKind
import io.github.bbsk.c5d.model.reference.enums.Profile
import io.github.bbsk.c5d.model.reference.enums.WorkTypeCode
import io.github.bbsk.c5d.repository.WorkTypeRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class WorkTypeServiceTest {

    @Mock
    private lateinit var workTypeRepository: WorkTypeRepository

    private lateinit var workTypeService: WorkTypeService

    @BeforeEach
    fun setUp() {
        workTypeService = WorkTypeService(workTypeRepository)
    }

    @Test
    fun `create work type successfully saves and returns response`() {
        val request = CreateWorkTypeRequest(
            code = WorkTypeCode.INST_PON_MKD,
            name = "Инсталляция ШПД/IPTV, МКД, PON",
            durationMin = 90,
            maxStartDelayMin = 10,
            deadlineKind = DeadlineKind.WORKING_DAYS,
            deadlineValue = 3,
            repeatDeadlineValue = null,
            pgpRelevant = false,
            profiles = setOf(Profile.INSTALL)
        )

        `when`(workTypeRepository.existsByCode(WorkTypeCode.INST_PON_MKD)).thenReturn(false)
        `when`(workTypeRepository.save(any(WorkType::class.java))).thenAnswer { it.getArgument(0) as WorkType }

        val response = workTypeService.create(request)

        assertNotNull(response)
        assertNotNull(response.id)
        assertEquals(WorkTypeCode.INST_PON_MKD, response.code)
        assertEquals("Инсталляция ШПД/IPTV, МКД, PON", response.name)
        assertEquals(90, response.durationMin)
        assertEquals(10, response.maxStartDelayMin)
        assertEquals(DeadlineKind.WORKING_DAYS, response.deadlineKind)
        assertEquals(3, response.deadlineValue)
        assertFalse(response.pgpRelevant)
        assertTrue(response.profiles.contains(Profile.INSTALL))
        verify(workTypeRepository, times(1)).save(any(WorkType::class.java))
    }

    @Test
    fun `create work type throws ConflictException if code already exists`() {
        val request = CreateWorkTypeRequest(
            code = WorkTypeCode.INST_PON_MKD,
            name = "Инсталляция PON",
            durationMin = 90,
            deadlineKind = DeadlineKind.WORKING_DAYS,
            deadlineValue = 3,
            profiles = setOf(Profile.INSTALL)
        )

        `when`(workTypeRepository.existsByCode(WorkTypeCode.INST_PON_MKD)).thenReturn(true)

        val ex = assertThrows(ConflictException::class.java) {
            workTypeService.create(request)
        }
        assertEquals("WORK_TYPE_ALREADY_EXISTS", ex.code)
    }

    @Test
    fun `getById returns work type when found`() {
        val id = UUID.randomUUID()
        val entity = WorkType(
            id = id,
            code = WorkTypeCode.REPAIR,
            name = "Устранение неисправности",
            durationMin = 60,
            deadlineKind = DeadlineKind.HOURS,
            deadlineValue = 24,
            repeatDeadlineValue = 12,
            pgpRelevant = true,
            profiles = mutableSetOf(Profile.LTP3)
        )

        `when`(workTypeRepository.findById(id)).thenReturn(Optional.of(entity))

        val response = workTypeService.getById(id)

        assertNotNull(response)
        assertEquals(id, response.id)
        assertEquals(WorkTypeCode.REPAIR, response.code)
        assertTrue(response.pgpRelevant)
        assertEquals(12, response.repeatDeadlineValue)
    }

    @Test
    fun `getById throws NotFoundException when not found`() {
        val id = UUID.randomUUID()
        `when`(workTypeRepository.findById(id)).thenReturn(Optional.empty())

        val ex = assertThrows(NotFoundException::class.java) {
            workTypeService.getById(id)
        }
        assertEquals("WORK_TYPE_NOT_FOUND", ex.code)
    }

    @Test
    fun `getByCode returns work type when found`() {
        val entity = WorkType(
            code = WorkTypeCode.SURVEY_PRIVATE,
            name = "Обследование",
            durationMin = 45,
            deadlineKind = DeadlineKind.WORKING_DAYS,
            deadlineValue = 2,
            profiles = mutableSetOf(Profile.INSTALL, Profile.LTP3)
        )

        `when`(workTypeRepository.findByCode(WorkTypeCode.SURVEY_PRIVATE)).thenReturn(entity)

        val response = workTypeService.getByCode(WorkTypeCode.SURVEY_PRIVATE)

        assertNotNull(response)
        assertEquals(WorkTypeCode.SURVEY_PRIVATE, response.code)
        assertEquals(2, response.profiles.size)
    }

    @Test
    fun `getByCode throws NotFoundException when not found`() {
        `when`(workTypeRepository.findByCode(WorkTypeCode.SURVEY_PRIVATE)).thenReturn(null)

        val ex = assertThrows(NotFoundException::class.java) {
            workTypeService.getByCode(WorkTypeCode.SURVEY_PRIVATE)
        }
        assertEquals("WORK_TYPE_NOT_FOUND", ex.code)
    }

    @Test
    fun `update work type successfully updates fields`() {
        val id = UUID.randomUUID()
        val existing = WorkType(
            id = id,
            code = WorkTypeCode.INST_PON_MKD,
            name = "Старое имя",
            durationMin = 90,
            deadlineKind = DeadlineKind.WORKING_DAYS,
            deadlineValue = 3,
            profiles = mutableSetOf(Profile.INSTALL)
        )

        `when`(workTypeRepository.findById(id)).thenReturn(Optional.of(existing))
        `when`(workTypeRepository.save(any(WorkType::class.java))).thenAnswer { it.getArgument(0) as WorkType }

        val request = UpdateWorkTypeRequest(
            name = "Новое имя",
            durationMin = 100,
            profiles = setOf(Profile.INSTALL, Profile.LTP3)
        )

        val updated = workTypeService.update(id, request)

        assertEquals("Новое имя", updated.name)
        assertEquals(100, updated.durationMin)
        assertEquals(2, updated.profiles.size)
    }

    @Test
    fun `update work type throws ConflictException if new code already exists`() {
        val id = UUID.randomUUID()
        val existing = WorkType(
            id = id,
            code = WorkTypeCode.INST_PON_MKD,
            name = "Имя",
            durationMin = 90,
            deadlineKind = DeadlineKind.WORKING_DAYS,
            profiles = mutableSetOf(Profile.INSTALL)
        )

        `when`(workTypeRepository.findById(id)).thenReturn(Optional.of(existing))
        `when`(workTypeRepository.existsByCode(WorkTypeCode.REPAIR)).thenReturn(true)

        val request = UpdateWorkTypeRequest(code = WorkTypeCode.REPAIR)

        val ex = assertThrows(ConflictException::class.java) {
            workTypeService.update(id, request)
        }
        assertEquals("WORK_TYPE_ALREADY_EXISTS", ex.code)
    }

    @Test
    fun `delete work type removes entity`() {
        val id = UUID.randomUUID()
        val existing = WorkType(
            id = id,
            code = WorkTypeCode.DISMANTLE,
            name = "Демонтаж",
            durationMin = 30,
            deadlineKind = DeadlineKind.WORKING_DAYS,
            profiles = mutableSetOf(Profile.LTP3)
        )

        `when`(workTypeRepository.findById(id)).thenReturn(Optional.of(existing))

        workTypeService.delete(id)

        verify(workTypeRepository, times(1)).delete(existing)
    }

    @Test
    fun `delete work type throws NotFoundException when not found`() {
        val id = UUID.randomUUID()
        `when`(workTypeRepository.findById(id)).thenReturn(Optional.empty())

        val ex = assertThrows(NotFoundException::class.java) {
            workTypeService.delete(id)
        }
        assertEquals("WORK_TYPE_NOT_FOUND", ex.code)
    }
}
