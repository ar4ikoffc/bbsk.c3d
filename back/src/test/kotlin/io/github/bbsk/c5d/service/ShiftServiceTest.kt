package io.github.bbsk.c5d.service

import io.github.bbsk.c5d.exception.ConflictException
import io.github.bbsk.c5d.exception.NotFoundException
import io.github.bbsk.c5d.model.reference.dto.BreakWindowDto
import io.github.bbsk.c5d.model.reference.dto.request.CreateShiftRequest
import io.github.bbsk.c5d.model.reference.entity.Shift
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
class ShiftServiceTest {

    @Mock
    private lateinit var shiftRepository: ShiftRepository

    @Mock
    private lateinit var staffRepository: StaffRepository

    private lateinit var shiftService: ShiftService

    @BeforeEach
    fun setUp() {
        shiftService = ShiftService(shiftRepository, staffRepository)
    }

    @Test
    fun `create shift successfully saves and returns ShiftResponse with id`() {
        val request = CreateShiftRequest(
            start = LocalTime.of(9, 0),
            end = LocalTime.of(18, 0),
            breakDurationMin = 60,
            breakWindow = BreakWindowDto(from = LocalTime.of(12, 0), to = LocalTime.of(15, 0))
        )

        `when`(shiftRepository.findFirstByStartTimeAndEndTimeAndBreakDurationMinAndBreakWindowFromAndBreakWindowTo(
            any(), any(), anyInt(), any(), any()
        )).thenReturn(null)

        `when`(shiftRepository.save(any(Shift::class.java))).thenAnswer { it.getArgument(0) as Shift }

        val response = shiftService.create(request)

        assertNotNull(response)
        assertNotNull(response.id)
        assertEquals(LocalTime.of(9, 0), response.start)
        assertEquals(LocalTime.of(18, 0), response.end)
        assertEquals(60, response.breakDurationMin)
        assertEquals(LocalTime.of(12, 0), response.breakWindow.from)
        verify(shiftRepository, times(1)).save(any(Shift::class.java))
    }

    @Test
    fun `getById returns shift when exists`() {
        val shiftId = UUID.randomUUID()
        val shift = Shift(
            id = shiftId,
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(18, 0),
            breakDurationMin = 60,
            breakWindowFrom = LocalTime.of(12, 0),
            breakWindowTo = LocalTime.of(15, 0)
        )

        `when`(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift))

        val response = shiftService.getById(shiftId)

        assertEquals(shiftId, response.id)
        assertEquals(LocalTime.of(9, 0), response.start)
    }

    @Test
    fun `getById throws NotFoundException when not found`() {
        val unknownId = UUID.randomUUID()
        `when`(shiftRepository.findById(unknownId)).thenReturn(Optional.empty())

        val ex = assertThrows(NotFoundException::class.java) {
            shiftService.getById(unknownId)
        }

        assertEquals("SHIFT_NOT_FOUND", ex.code)
    }

    @Test
    fun `delete shift removes entity when not in use`() {
        val shiftId = UUID.randomUUID()
        val shift = Shift(
            id = shiftId,
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(18, 0),
            breakDurationMin = 60,
            breakWindowFrom = LocalTime.of(12, 0),
            breakWindowTo = LocalTime.of(15, 0)
        )

        `when`(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift))
        `when`(staffRepository.existsByShiftId(shiftId)).thenReturn(false)

        shiftService.delete(shiftId)

        verify(shiftRepository, times(1)).delete(shift)
    }

    @Test
    fun `delete shift throws ConflictException when shift is in use by staff`() {
        val shiftId = UUID.randomUUID()
        val shift = Shift(
            id = shiftId,
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(18, 0),
            breakDurationMin = 60,
            breakWindowFrom = LocalTime.of(12, 0),
            breakWindowTo = LocalTime.of(15, 0)
        )

        `when`(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift))
        `when`(staffRepository.existsByShiftId(shiftId)).thenReturn(true)

        val ex = assertThrows(ConflictException::class.java) {
            shiftService.delete(shiftId)
        }

        assertEquals("SHIFT_IN_USE", ex.code)
        verify(shiftRepository, never()).delete(any())
    }
}
