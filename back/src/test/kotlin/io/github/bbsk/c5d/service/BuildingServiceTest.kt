package io.github.bbsk.c5d.service

import io.github.bbsk.c5d.exception.ConflictException
import io.github.bbsk.c5d.exception.NotFoundException
import io.github.bbsk.c5d.model.reference.dto.request.CreateBuildingRequest
import io.github.bbsk.c5d.model.reference.dto.request.UpdateBuildingRequest
import io.github.bbsk.c5d.model.reference.entity.AccessNode
import io.github.bbsk.c5d.model.reference.entity.Building
import io.github.bbsk.c5d.model.reference.entity.ServiceCenter
import io.github.bbsk.c5d.model.reference.enums.AccessNodeType
import io.github.bbsk.c5d.model.reference.enums.BuildingType
import io.github.bbsk.c5d.model.reference.enums.Technology
import io.github.bbsk.c5d.repository.AccessNodeRepository
import io.github.bbsk.c5d.repository.BuildingRepository
import io.github.bbsk.c5d.repository.ServiceCenterRepository
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
class BuildingServiceTest {

    @Mock
    private lateinit var buildingRepository: BuildingRepository

    @Mock
    private lateinit var accessNodeRepository: AccessNodeRepository

    @Mock
    private lateinit var serviceCenterRepository: ServiceCenterRepository

    private lateinit var buildingService: BuildingService

    private val sampleAccessNode = AccessNode(
        id = UUID.randomUUID(),
        code = "AN-0031",
        nodeType = AccessNodeType.OLT,
        technology = Technology.PON,
        name = "OLT",
        address = "ул. Учителей, 22",
        lat = 56.8698,
        lon = 60.6510
    )

    private val sampleServiceCenter = ServiceCenter(
        id = UUID.randomUUID(),
        code = "SC-01",
        name = "СЦ Север",
        address = "ул. Победы, 30",
        lat = 56.8941,
        lon = 60.6105,
        openTime = LocalTime.of(8, 0),
        closeTime = LocalTime.of(21, 0),
        restockDurationMin = 15
    )

    @BeforeEach
    fun setUp() {
        buildingService = BuildingService(buildingRepository, accessNodeRepository, serviceCenterRepository)
    }

    @Test
    fun `create building successfully saves and returns response`() {
        val request = CreateBuildingRequest(
            buildingId = "B-00412",
            accessNodeId = sampleAccessNode.id,
            zoneServiceCenterId = sampleServiceCenter.id,
            address = "г. Екатеринбург, ул. Учителей, 18",
            lat = 56.8712,
            lon = 60.6531,
            buildingType = BuildingType.MKD,
            overheadLine = false
        )

        `when`(buildingRepository.existsByCode("B-00412")).thenReturn(false)
        `when`(accessNodeRepository.findById(sampleAccessNode.id)).thenReturn(Optional.of(sampleAccessNode))
        `when`(serviceCenterRepository.findById(sampleServiceCenter.id)).thenReturn(Optional.of(sampleServiceCenter))
        `when`(buildingRepository.save(any(Building::class.java))).thenAnswer { it.getArgument(0) as Building }

        val response = buildingService.create(request)

        assertNotNull(response)
        assertNotNull(response.id)
        assertEquals("B-00412", response.buildingId)
        assertEquals(sampleAccessNode.id, response.accessNodeId)
        assertEquals(sampleServiceCenter.id, response.zoneServiceCenterId)
        assertEquals("г. Екатеринбург, ул. Учителей, 18", response.address)
        assertEquals(BuildingType.MKD, response.buildingType)
        assertFalse(response.overheadLine)
        verify(buildingRepository, times(1)).save(any(Building::class.java))
    }

    @Test
    fun `create building throws ConflictException when code exists`() {
        val request = CreateBuildingRequest(
            buildingId = "B-00412",
            accessNodeId = sampleAccessNode.id,
            address = "Адрес",
            lat = 56.0,
            lon = 60.0,
            buildingType = BuildingType.MKD
        )

        `when`(buildingRepository.existsByCode("B-00412")).thenReturn(true)

        val ex = assertThrows(ConflictException::class.java) {
            buildingService.create(request)
        }
        assertEquals("BUILDING_ALREADY_EXISTS", ex.code)
    }

    @Test
    fun `create building throws NotFoundException when access node not found`() {
        val request = CreateBuildingRequest(
            buildingId = "B-00412",
            accessNodeId = sampleAccessNode.id,
            address = "Адрес",
            lat = 56.0,
            lon = 60.0,
            buildingType = BuildingType.MKD
        )

        `when`(buildingRepository.existsByCode("B-00412")).thenReturn(false)
        `when`(accessNodeRepository.findById(sampleAccessNode.id)).thenReturn(Optional.empty())

        val ex = assertThrows(NotFoundException::class.java) {
            buildingService.create(request)
        }
        assertEquals("ACCESS_NODE_NOT_FOUND", ex.code)
    }

    @Test
    fun `getById returns building when found`() {
        val id = UUID.randomUUID()
        val building = Building(
            id = id,
            code = "B-00412",
            accessNode = sampleAccessNode,
            zoneServiceCenter = sampleServiceCenter,
            address = "ул. Учителей, 18",
            lat = 56.8712,
            lon = 60.6531,
            buildingType = BuildingType.MKD,
            overheadLine = true
        )

        `when`(buildingRepository.findById(id)).thenReturn(Optional.of(building))

        val response = buildingService.getById(id)

        assertNotNull(response)
        assertEquals(id, response.id)
        assertEquals("B-00412", response.buildingId)
        assertTrue(response.overheadLine)
    }

    @Test
    fun `delete building deletes when found`() {
        val id = UUID.randomUUID()
        val building = Building(
            id = id,
            code = "B-00412",
            accessNode = sampleAccessNode,
            address = "ул. Учителей, 18",
            lat = 56.8712,
            lon = 60.6531,
            buildingType = BuildingType.MKD
        )

        `when`(buildingRepository.findById(id)).thenReturn(Optional.of(building))

        buildingService.delete(id)

        verify(buildingRepository, times(1)).delete(building)
    }
}
