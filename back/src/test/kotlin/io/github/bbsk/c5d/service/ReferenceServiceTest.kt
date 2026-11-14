package io.github.bbsk.c5d.service

import io.github.bbsk.c5d.model.reference.dto.BreakWindowDto
import io.github.bbsk.c5d.model.reference.dto.EquipmentDto
import io.github.bbsk.c5d.model.reference.dto.OpeningHoursDto
import io.github.bbsk.c5d.model.reference.dto.ShiftDto
import io.github.bbsk.c5d.model.reference.dto.bulk.ReferenceAccessNodeDto
import io.github.bbsk.c5d.model.reference.dto.bulk.ReferenceBuildingDto
import io.github.bbsk.c5d.model.reference.dto.bulk.ReferenceDataRequest
import io.github.bbsk.c5d.model.reference.dto.bulk.ReferenceStaffDto
import io.github.bbsk.c5d.model.reference.dto.request.CreateServiceCenterRequest
import io.github.bbsk.c5d.model.reference.dto.request.CreateWorkTypeRequest
import io.github.bbsk.c5d.model.reference.enums.*
import io.github.bbsk.c5d.repository.*
import jakarta.persistence.EntityManager
import jakarta.persistence.Query
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalTime

@ExtendWith(MockitoExtension::class)
class ReferenceServiceTest {

    @Mock
    private lateinit var entityManager: EntityManager

    @Mock
    private lateinit var query: Query

    @Mock
    private lateinit var serviceCenterRepository: ServiceCenterRepository

    @Mock
    private lateinit var accessNodeRepository: AccessNodeRepository

    @Mock
    private lateinit var buildingRepository: BuildingRepository

    @Mock
    private lateinit var workTypeRepository: WorkTypeRepository

    @Mock
    private lateinit var shiftRepository: ShiftRepository

    @Mock
    private lateinit var staffRepository: StaffRepository

    private lateinit var referenceService: ReferenceService

    @BeforeEach
    fun setUp() {
        referenceService = ReferenceService(
            entityManager,
            serviceCenterRepository,
            accessNodeRepository,
            buildingRepository,
            workTypeRepository,
            shiftRepository,
            staffRepository
        )
    }

    @Test
    fun `loadReferenceData cleans up db, saves all entities and returns counts`() {
        `when`(entityManager.createNativeQuery(anyString())).thenReturn(query)
        `when`(query.executeUpdate()).thenReturn(1)

        val request = ReferenceDataRequest(
            serviceCenters = listOf(
                CreateServiceCenterRequest(
                    serviceCenterId = "SC-01",
                    name = "СЦ Север",
                    address = "ул. Победы, 30",
                    lat = 56.89,
                    lon = 60.61,
                    openingHours = OpeningHoursDto(open = LocalTime.of(8, 0), close = LocalTime.of(21, 0)),
                    restockDurationMin = 15
                )
            ),
            staff = listOf(
                ReferenceStaffDto(
                    staffId = "TECH-01",
                    name = "Иванов Иван",
                    serviceCenterId = "SC-01",
                    shift = ShiftDto(
                        start = LocalTime.of(8, 0),
                        end = LocalTime.of(20, 0),
                        breakDurationMin = 60,
                        breakWindow = BreakWindowDto(
                            from = LocalTime.of(12, 0),
                            to = LocalTime.of(15, 0)
                        )
                    ),
                    profiles = setOf(Profile.INSTALL),
                    technologies = setOf(Technology.PON),
                    equipmentStart = EquipmentDto(ont = 4, router = 2, stb = 2),
                    equipmentCapacity = EquipmentDto(ont = 6, router = 4, stb = 4),
                    heightPermit = false,
                    experienceYears = 3.0
                )
            ),
            accessNodes = listOf(
                ReferenceAccessNodeDto(
                    accessNodeId = "AN-0031",
                    nodeType = AccessNodeType.OLT,
                    technology = Technology.PON,
                    name = "OLT Пионерский-2",
                    address = "ул. Учителей, 22",
                    lat = 56.86,
                    lon = 60.65,
                    buildings = listOf(
                        ReferenceBuildingDto(
                            buildingId = "B-00412",
                            address = "ул. Учителей, 18",
                            lat = 56.87,
                            lon = 60.65,
                            buildingType = BuildingType.MKD,
                            overheadLine = false,
                            zoneServiceCenterId = "SC-01"
                        )
                    )
                )
            ),
            workTypes = listOf(
                CreateWorkTypeRequest(
                    code = WorkTypeCode.INST_PON_MKD,
                    name = "Инсталляция PON",
                    durationMin = 90,
                    deadlineKind = DeadlineKind.WORKING_DAYS,
                    deadlineValue = 3,
                    pgpRelevant = false,
                    profiles = setOf(Profile.INSTALL)
                )
            )
        )

        val result = referenceService.loadReferenceData(request)

        assertEquals("loaded", result.status)
        assertEquals(1, result.counts["service_centers"])
        assertEquals(1, result.counts["staff"])
        assertEquals(1, result.counts["access_nodes"])
        assertEquals(1, result.counts["buildings"])
        assertEquals(1, result.counts["work_types"])

        verify(serviceCenterRepository, times(1)).saveAll(any<Iterable<Any>>())
        verify(accessNodeRepository, times(1)).saveAll(any<Iterable<Any>>())
        verify(buildingRepository, times(1)).saveAll(any<Iterable<Any>>())
        verify(workTypeRepository, times(1)).saveAll(any<Iterable<Any>>())
        verify(shiftRepository, times(1)).saveAll(any<Iterable<Any>>())
        verify(staffRepository, times(1)).saveAll(any<Iterable<Any>>())
    }
}
