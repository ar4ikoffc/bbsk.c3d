package io.github.bbsk.c5d.service

import io.github.bbsk.c5d.exception.ConflictException
import io.github.bbsk.c5d.exception.NotFoundException
import io.github.bbsk.c5d.model.reference.dto.OpeningHoursDto
import io.github.bbsk.c5d.model.reference.dto.request.CreateServiceCenterRequest
import io.github.bbsk.c5d.model.reference.dto.request.UpdateServiceCenterRequest
import io.github.bbsk.c5d.model.reference.entity.ServiceCenter
import io.github.bbsk.c5d.repository.ServiceCenterRepository
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
class ServiceCenterServiceTest {

    @Mock
    private lateinit var serviceCenterRepository: ServiceCenterRepository

    @Mock
    private lateinit var staffRepository: StaffRepository

    private lateinit var serviceCenterService: ServiceCenterService

    @BeforeEach
    fun setUp() {
        serviceCenterService = ServiceCenterService(serviceCenterRepository, staffRepository)
    }

    @Test
    fun `create service center successfully saves and returns response with id`() {
        val request = CreateServiceCenterRequest(
            serviceCenterId = "SC-01",
            name = "СЦ Север",
            address = "г. Екатеринбург, ул. Победы, 30",
            lat = 56.8941,
            lon = 60.6105,
            openingHours = OpeningHoursDto(open = LocalTime.of(8, 0), close = LocalTime.of(21, 0)),
            restockDurationMin = 15
        )

        `when`(serviceCenterRepository.existsByCode("SC-01")).thenReturn(false)
        `when`(serviceCenterRepository.save(any(ServiceCenter::class.java))).thenAnswer { it.getArgument(0) as ServiceCenter }

        val response = serviceCenterService.create(request)

        assertNotNull(response)
        assertNotNull(response.id)
        assertEquals("SC-01", response.serviceCenterId)
        assertEquals("СЦ Север", response.name)
        assertEquals(56.8941, response.lat)
        assertEquals(15, response.restockDurationMin)
        verify(serviceCenterRepository, times(1)).save(any(ServiceCenter::class.java))
    }

    @Test
    fun `create service center throws ConflictException when code exists`() {
        val request = CreateServiceCenterRequest(
            serviceCenterId = "SC-01",
            name = "СЦ Север",
            address = "г. Екатеринбург, ул. Победы, 30",
            lat = 56.8941,
            lon = 60.6105,
            openingHours = OpeningHoursDto(open = LocalTime.of(8, 0), close = LocalTime.of(21, 0))
        )

        `when`(serviceCenterRepository.existsByCode("SC-01")).thenReturn(true)

        val ex = assertThrows(ConflictException::class.java) {
            serviceCenterService.create(request)
        }

        assertEquals("SERVICE_CENTER_ALREADY_EXISTS", ex.code)
        verify(serviceCenterRepository, never()).save(any(ServiceCenter::class.java))
    }

    @Test
    fun `getById returns service center when exists`() {
        val scId = UUID.randomUUID()
        val sc = ServiceCenter(
            id = scId,
            code = "SC-01",
            name = "СЦ Север",
            address = "г. Екатеринбург, ул. Победы, 30",
            lat = 56.8941,
            lon = 60.6105,
            openTime = LocalTime.of(8, 0),
            closeTime = LocalTime.of(21, 0),
            restockDurationMin = 15
        )

        `when`(serviceCenterRepository.findById(scId)).thenReturn(Optional.of(sc))

        val response = serviceCenterService.getById(scId)

        assertEquals(scId, response.id)
        assertEquals("SC-01", response.serviceCenterId)
        assertEquals("СЦ Север", response.name)
    }

    @Test
    fun `getById throws NotFoundException when id not found`() {
        val unknownId = UUID.randomUUID()
        `when`(serviceCenterRepository.findById(unknownId)).thenReturn(Optional.empty())

        val ex = assertThrows(NotFoundException::class.java) {
            serviceCenterService.getById(unknownId)
        }

        assertEquals("SERVICE_CENTER_NOT_FOUND", ex.code)
    }

    @Test
    fun `update service center modifies fields`() {
        val scId = UUID.randomUUID()
        val sc = ServiceCenter(
            id = scId,
            code = "SC-01",
            name = "СЦ Север",
            address = "г. Екатеринбург, ул. Победы, 30",
            lat = 56.8941,
            lon = 60.6105,
            openTime = LocalTime.of(8, 0),
            closeTime = LocalTime.of(21, 0),
            restockDurationMin = 15
        )

        val updateReq = UpdateServiceCenterRequest(
            name = "СЦ Север (Новый)",
            restockDurationMin = 20
        )

        `when`(serviceCenterRepository.findById(scId)).thenReturn(Optional.of(sc))
        `when`(serviceCenterRepository.save(any(ServiceCenter::class.java))).thenAnswer { it.getArgument(0) as ServiceCenter }

        val response = serviceCenterService.update(scId, updateReq)

        assertEquals("СЦ Север (Новый)", response.name)
        assertEquals(20, response.restockDurationMin)
    }

    @Test
    fun `delete service center removes entity`() {
        val scId = UUID.randomUUID()
        val sc = ServiceCenter(
            id = scId,
            code = "SC-01",
            name = "СЦ Север",
            address = "г. Екатеринбург, ул. Победы, 30",
            lat = 56.8941,
            lon = 60.6105,
            openTime = LocalTime.of(8, 0),
            closeTime = LocalTime.of(21, 0)
        )

        `when`(serviceCenterRepository.findById(scId)).thenReturn(Optional.of(sc))
        `when`(staffRepository.existsByServiceCenterId(scId)).thenReturn(false)

        serviceCenterService.delete(scId)

        verify(serviceCenterRepository, times(1)).delete(sc)
    }

    @Test
    fun `delete service center throws ConflictException when staff assigned to it`() {
        val scId = UUID.randomUUID()
        val sc = ServiceCenter(
            id = scId,
            code = "SC-01",
            name = "СЦ Север",
            address = "г. Екатеринбург, ул. Победы, 30",
            lat = 56.8941,
            lon = 60.6105,
            openTime = LocalTime.of(8, 0),
            closeTime = LocalTime.of(21, 0)
        )

        `when`(serviceCenterRepository.findById(scId)).thenReturn(Optional.of(sc))
        `when`(staffRepository.existsByServiceCenterId(scId)).thenReturn(true)

        val ex = assertThrows(ConflictException::class.java) {
            serviceCenterService.delete(scId)
        }

        assertEquals("SERVICE_CENTER_IN_USE", ex.code)
        verify(serviceCenterRepository, never()).delete(any())
    }
}
