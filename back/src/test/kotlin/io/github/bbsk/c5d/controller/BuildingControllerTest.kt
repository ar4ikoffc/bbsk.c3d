package io.github.bbsk.c5d.controller

import io.github.bbsk.c5d.config.JwtAuthenticationFilter
import io.github.bbsk.c5d.config.JwtService
import io.github.bbsk.c5d.config.SecurityConfig
import io.github.bbsk.c5d.exception.ConflictException
import io.github.bbsk.c5d.exception.GlobalExceptionHandler
import io.github.bbsk.c5d.exception.NotFoundException
import io.github.bbsk.c5d.model.reference.dto.OpeningHoursDto
import io.github.bbsk.c5d.model.reference.dto.response.AccessNodeResponse
import io.github.bbsk.c5d.model.reference.dto.response.BuildingResponse
import io.github.bbsk.c5d.model.reference.dto.response.ServiceCenterResponse
import io.github.bbsk.c5d.model.reference.enums.AccessNodeType
import io.github.bbsk.c5d.model.reference.enums.BuildingType
import io.github.bbsk.c5d.model.reference.enums.Technology
import io.github.bbsk.c5d.service.AccessNodeService
import io.github.bbsk.c5d.service.BuildingService
import io.github.bbsk.c5d.service.ServiceCenterService
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalTime
import java.util.UUID

@WebMvcTest(BuildingController::class)
@Import(GlobalExceptionHandler::class, SecurityConfig::class, JwtAuthenticationFilter::class)
class BuildingControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var buildingService: BuildingService

    @MockitoBean
    private lateinit var accessNodeService: AccessNodeService

    @MockitoBean
    private lateinit var serviceCenterService: ServiceCenterService

    @MockitoBean
    private lateinit var jwtService: JwtService

    private val sampleBuildingId = UUID.randomUUID()
    private val sampleAccessNodeId = UUID.randomUUID()
    private val sampleServiceCenterId = UUID.randomUUID()

    private val sampleBuildingResponse = BuildingResponse(
        id = sampleBuildingId,
        buildingId = "B-00412",
        accessNodeId = sampleAccessNodeId,
        accessNodeCode = "AN-0031",
        zoneServiceCenterId = sampleServiceCenterId,
        zoneServiceCenterCode = "SC-01",
        address = "г. Екатеринбург, ул. Учителей, 18",
        lat = 56.8712,
        lon = 60.6531,
        buildingType = BuildingType.MKD,
        overheadLine = false
    )

    private val sampleAccessNodeResponse = AccessNodeResponse(
        id = sampleAccessNodeId,
        accessNodeId = "AN-0031",
        nodeType = AccessNodeType.OLT,
        technology = Technology.PON,
        name = "OLT Пионерский-2",
        address = "г. Екатеринбург, ул. Учителей, 22",
        lat = 56.8698,
        lon = 60.6510
    )

    private val sampleServiceCenterResponse = ServiceCenterResponse(
        id = sampleServiceCenterId,
        serviceCenterId = "SC-01",
        name = "СЦ Север",
        address = "г. Екатеринбург, ул. Победы, 30",
        lat = 56.8941,
        lon = 60.6105,
        openingHours = OpeningHoursDto(open = LocalTime.of(8, 0), close = LocalTime.of(21, 0)),
        restockDurationMin = 15
    )

    // ==========================================
    // ЗДАНИЯ (BUILDINGS)
    // ==========================================

    @Test
    fun `GET buildings returns 200 and list`() {
        `when`(buildingService.getAll(any(), any(), any())).thenReturn(listOf(sampleBuildingResponse))

        mockMvc.perform(get("/api/v1/buildings"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(sampleBuildingId.toString()))
            .andExpect(jsonPath("$[0].building_id").value("B-00412"))
            .andExpect(jsonPath("$[0].building_type").value("MKD"))
    }

    @Test
    fun `GET building by id returns 200 when found`() {
        `when`(buildingService.getById(sampleBuildingId)).thenReturn(sampleBuildingResponse)

        mockMvc.perform(get("/api/v1/buildings/$sampleBuildingId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(sampleBuildingId.toString()))
            .andExpect(jsonPath("$.building_id").value("B-00412"))
    }

    @Test
    fun `GET building by id returns 404 when not found`() {
        val nonExistentId = UUID.randomUUID()
        `when`(buildingService.getById(nonExistentId))
            .thenThrow(NotFoundException("BUILDING_NOT_FOUND", "Здание не найдено"))

        mockMvc.perform(get("/api/v1/buildings/$nonExistentId"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.code").value("BUILDING_NOT_FOUND"))
    }

    @Test
    fun `POST building returns 201 on valid body`() {
        `when`(buildingService.create(any())).thenReturn(sampleBuildingResponse)

        val json = """
            {
                "building_id": "B-00412",
                "access_node_id": "$sampleAccessNodeId",
                "zone_service_center_id": "$sampleServiceCenterId",
                "address": "г. Екатеринбург, ул. Учителей, 18",
                "lat": 56.8712,
                "lon": 60.6531,
                "building_type": "MKD",
                "overhead_line": false
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/buildings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(sampleBuildingId.toString()))
            .andExpect(jsonPath("$.building_id").value("B-00412"))
    }

    @Test
    fun `POST building returns 400 on invalid body`() {
        val json = """
            {
                "building_id": "",
                "lat": 1000.0,
                "building_type": "MKD"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/buildings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
    }

    @Test
    fun `DELETE building returns 204 on success`() {
        doNothing().`when`(buildingService).delete(sampleBuildingId)

        mockMvc.perform(delete("/api/v1/buildings/$sampleBuildingId"))
            .andExpect(status().isNoContent)
    }

    // ==========================================
    // УЗЛЫ ДОСТУПА (ACCESS NODES)
    // ==========================================

    @Test
    fun `GET access-nodes returns 200 and list`() {
        `when`(accessNodeService.getAll(any())).thenReturn(listOf(sampleAccessNodeResponse))

        mockMvc.perform(get("/api/v1/buildings/access-nodes"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(sampleAccessNodeId.toString()))
            .andExpect(jsonPath("$[0].access_node_id").value("AN-0031"))
            .andExpect(jsonPath("$[0].node_type").value("OLT"))
    }

    @Test
    fun `GET access-node by id returns 200 when found`() {
        `when`(accessNodeService.getById(sampleAccessNodeId)).thenReturn(sampleAccessNodeResponse)

        mockMvc.perform(get("/api/v1/buildings/access-nodes/$sampleAccessNodeId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(sampleAccessNodeId.toString()))
            .andExpect(jsonPath("$.technology").value("PON"))
    }

    @Test
    fun `POST access-node returns 201 on valid body`() {
        `when`(accessNodeService.create(any())).thenReturn(sampleAccessNodeResponse)

        val json = """
            {
                "access_node_id": "AN-0031",
                "node_type": "OLT",
                "technology": "PON",
                "name": "OLT Пионерский-2",
                "address": "г. Екатеринбург, ул. Учителей, 22",
                "lat": 56.8698,
                "lon": 60.6510
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/buildings/access-nodes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(sampleAccessNodeId.toString()))
            .andExpect(jsonPath("$.access_node_id").value("AN-0031"))
    }

    @Test
    fun `DELETE access-node returns 409 when in use`() {
        doThrow(ConflictException("ACCESS_NODE_IN_USE", "Узел доступа используется"))
            .`when`(accessNodeService).delete(sampleAccessNodeId)

        mockMvc.perform(delete("/api/v1/buildings/access-nodes/$sampleAccessNodeId"))
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.code").value("ACCESS_NODE_IN_USE"))
    }

    // ==========================================
    // СЕРВИСНЫЕ ЦЕНТРЫ (SERVICE CENTERS)
    // ==========================================

    @Test
    fun `GET service-centers returns 200 and list`() {
        `when`(serviceCenterService.getAll()).thenReturn(listOf(sampleServiceCenterResponse))

        mockMvc.perform(get("/api/v1/buildings/service-centers"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(sampleServiceCenterId.toString()))
            .andExpect(jsonPath("$[0].service_center_id").value("SC-01"))
    }

    @Test
    fun `GET service-center by id returns 200 when found`() {
        `when`(serviceCenterService.getById(sampleServiceCenterId)).thenReturn(sampleServiceCenterResponse)

        mockMvc.perform(get("/api/v1/buildings/service-centers/$sampleServiceCenterId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(sampleServiceCenterId.toString()))
            .andExpect(jsonPath("$.name").value("СЦ Север"))
    }

    @Test
    fun `POST service-center returns 201 on valid body`() {
        `when`(serviceCenterService.create(any())).thenReturn(sampleServiceCenterResponse)

        val json = """
            {
                "service_center_id": "SC-01",
                "name": "СЦ Север",
                "address": "г. Екатеринбург, ул. Победы, 30",
                "lat": 56.8941,
                "lon": 60.6105,
                "opening_hours": {
                    "open": "08:00:00",
                    "close": "21:00:00"
                },
                "restock_duration_min": 15
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/buildings/service-centers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(sampleServiceCenterId.toString()))
            .andExpect(jsonPath("$.service_center_id").value("SC-01"))
    }

    @Test
    fun `DELETE service-center returns 204 on success`() {
        doNothing().`when`(serviceCenterService).delete(sampleServiceCenterId)

        mockMvc.perform(delete("/api/v1/buildings/service-centers/$sampleServiceCenterId"))
            .andExpect(status().isNoContent)
    }
}
