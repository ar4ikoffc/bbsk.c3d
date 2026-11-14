package io.github.bbsk.c5d.service

import io.github.bbsk.c5d.exception.ConflictException
import io.github.bbsk.c5d.exception.NotFoundException
import io.github.bbsk.c5d.model.reference.dto.BreakWindowDto
import io.github.bbsk.c5d.model.reference.dto.EquipmentDto
import io.github.bbsk.c5d.model.reference.dto.ShiftDto
import io.github.bbsk.c5d.model.reference.dto.request.CreateStaffRequest
import io.github.bbsk.c5d.model.reference.entity.ServiceCenter
import io.github.bbsk.c5d.model.reference.entity.Shift
import io.github.bbsk.c5d.model.reference.entity.Staff
import io.github.bbsk.c5d.model.reference.enums.Profile
import io.github.bbsk.c5d.model.reference.enums.Technology
import io.github.bbsk.c5d.repository.ServiceCenterRepository
import io.github.bbsk.c5d.repository.ShiftRepository
import io.github.bbsk.c5d.repository.StaffRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalTime
import java.util.Optional
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class StaffServiceTest {

    @Mock
    private lateinit var staffRepository: StaffRepository

    @Mock
    private lateinit var serviceCenterRepository: ServiceCenterRepository

    @Mock
    private lateinit var shiftRepository: ShiftRepository

    private lateinit var staffService: StaffService

    private lateinit var mockServiceCenter: ServiceCenter
    private lateinit var mockShift: Shift

    @BeforeEach
    fun setUp() {
        staffService = StaffService(staffRepository, serviceCenterRepository, shiftRepository)

        mockServiceCenter = ServiceCenter(
            id = UUID.randomUUID(),
            code = "SC-01",
            name = "СЦ Север",
            address = "г. Екатеринбург, ул. Победы, 30",
            lat = 56.89,
            lon = 60.61,
            openTime = LocalTime.of(8, 0),
            closeTime = LocalTime.of(21, 0)
        )

        mockShift = Shift(
            id = UUID.randomUUID(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(18, 0),
            breakDurationMin = 60,
            breakWindowFrom = LocalTime.of(12, 0),
            breakWindowTo = LocalTime.of(15, 0)
        )
    }

    @Test
    fun `create staff successfully saves entity with correct attributes`() {
        val scId = mockServiceCenter.id
        val request = CreateStaffRequest(
            staffId = "ST-001",
            displayName = "Иванов Иван Иванович",
            serviceCenterId = scId,
            profiles = setOf(Profile.INSTALL, Profile.LTP3),
            technologies = setOf(Technology.PON, Technology.FTTB),
            heightPermit = true,
            experienceYears = 3.5,
            shift = ShiftDto(
                start = LocalTime.of(9, 0),
                end = LocalTime.of(18, 0),
                breakDurationMin = 60,
                breakWindow = BreakWindowDto(from = LocalTime.of(12, 0), to = LocalTime.of(15, 0))
            ),
            equipmentStart = EquipmentDto(ont = 2, router = 3, stb = 1),
            equipmentCapacity = EquipmentDto(ont = 5, router = 5, stb = 3)
        )

        `when`(staffRepository.existsByCode("ST-001")).thenReturn(false)
        `when`(serviceCenterRepository.findById(scId)).thenReturn(Optional.of(mockServiceCenter))
        `when`(shiftRepository.findFirstByStartTimeAndEndTimeAndBreakDurationMinAndBreakWindowFromAndBreakWindowTo(
            any(), any(), anyInt(), any(), any()
        )).thenReturn(mockShift)
        `when`(staffRepository.save(any(Staff::class.java))).thenAnswer { it.getArgument(0) as Staff }

        val response = staffService.create(request)

        assertNotNull(response)
        assertNotNull(response.id)
        assertEquals("ST-001", response.staffId)
        assertEquals("Иванов Иван Иванович", response.displayName)
        assertEquals(scId, response.serviceCenterId)
        assertEquals("SC-01", response.serviceCenterCode)
        assertTrue(response.heightPermit)
        assertEquals(3.5, response.experienceYears)
        assertEquals(2, response.equipmentStart.ont)
        assertEquals(5, response.equipmentCapacity.ont)
        assertTrue(response.profiles.contains(Profile.INSTALL))
        assertTrue(response.technologies.contains(Technology.PON))
        verify(staffRepository, times(1)).save(any(Staff::class.java))
    }

    @Test
    fun `create staff throws ConflictException when code already exists`() {
        val request = CreateStaffRequest(
            staffId = "ST-001",
            displayName = "Иванов Иван Иванович",
            serviceCenterId = mockServiceCenter.id,
            profiles = setOf(Profile.INSTALL),
            technologies = setOf(Technology.PON),
            shift = ShiftDto(
                start = LocalTime.of(9, 0),
                end = LocalTime.of(18, 0),
                breakDurationMin = 60,
                breakWindow = BreakWindowDto(from = LocalTime.of(12, 0), to = LocalTime.of(15, 0))
            )
        )

        `when`(staffRepository.existsByCode("ST-001")).thenReturn(true)

        val ex = assertThrows(ConflictException::class.java) {
            staffService.create(request)
        }

        assertEquals("STAFF_ALREADY_EXISTS", ex.code)
        verify(staffRepository, never()).save(any(Staff::class.java))
    }

    @Test
    fun `create staff throws NotFoundException when service center not found`() {
        val unknownId = UUID.randomUUID()
        val request = CreateStaffRequest(
            staffId = "ST-001",
            displayName = "Иванов Иван Иванович",
            serviceCenterId = unknownId,
            profiles = setOf(Profile.INSTALL),
            technologies = setOf(Technology.PON),
            shift = ShiftDto(
                start = LocalTime.of(9, 0),
                end = LocalTime.of(18, 0),
                breakDurationMin = 60,
                breakWindow = BreakWindowDto(from = LocalTime.of(12, 0), to = LocalTime.of(15, 0))
            )
        )

        `when`(staffRepository.existsByCode("ST-001")).thenReturn(false)
        `when`(serviceCenterRepository.findById(unknownId)).thenReturn(Optional.empty())

        val ex = assertThrows(NotFoundException::class.java) {
            staffService.create(request)
        }

        assertEquals("SERVICE_CENTER_NOT_FOUND", ex.code)
    }

    @Test
    fun `getById returns staff when exists`() {
        val staffId = UUID.randomUUID()
        val staff = Staff(
            id = staffId,
            code = "ST-001",
            displayName = "Монтажник 001",
            serviceCenter = mockServiceCenter,
            shift = mockShift,
            profiles = mutableSetOf(Profile.LTP3),
            technologies = mutableSetOf(Technology.PON)
        )

        `when`(staffRepository.findById(staffId)).thenReturn(Optional.of(staff))

        val response = staffService.getById(staffId)

        assertEquals(staffId, response.id)
        assertEquals("ST-001", response.staffId)
        assertEquals("Монтажник 001", response.displayName)
    }

    @Test
    fun `getById throws NotFoundException when does not exist`() {
        val unknownId = UUID.randomUUID()
        `when`(staffRepository.findById(unknownId)).thenReturn(Optional.empty())

        val ex = assertThrows(NotFoundException::class.java) {
            staffService.getById(unknownId)
        }

        assertEquals("STAFF_NOT_FOUND", ex.code)
    }

    @Test
    fun `delete staff removes entity from repository`() {
        val staffId = UUID.randomUUID()
        val staff = Staff(
            id = staffId,
            code = "ST-001",
            displayName = "Монтажник 001",
            serviceCenter = mockServiceCenter,
            shift = mockShift
        )

        `when`(staffRepository.findById(staffId)).thenReturn(Optional.of(staff))

        staffService.delete(staffId)

        verify(staffRepository, times(1)).delete(staff)
    }
}
