package io.github.bbsk.c5d.controller

import io.github.bbsk.c5d.config.JwtAuthenticationFilter
import io.github.bbsk.c5d.config.JwtService
import io.github.bbsk.c5d.config.SecurityConfig
import io.github.bbsk.c5d.exception.ConflictException
import io.github.bbsk.c5d.exception.GlobalExceptionHandler
import io.github.bbsk.c5d.exception.NotFoundException
import io.github.bbsk.c5d.model.reference.dto.BreakWindowDto
import io.github.bbsk.c5d.model.reference.dto.EquipmentDto
import io.github.bbsk.c5d.model.reference.dto.ShiftDto
import io.github.bbsk.c5d.model.reference.dto.response.StaffResponse
import io.github.bbsk.c5d.model.reference.enums.Profile
import io.github.bbsk.c5d.model.reference.enums.Technology
import io.github.bbsk.c5d.service.StaffService
import org.junit.jupiter.api.Test
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

@WebMvcTest(StaffController::class)
@Import(GlobalExceptionHandler::class, SecurityConfig::class, JwtAuthenticationFilter::class)
class StaffControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var staffService: StaffService

    @MockitoBean
    private lateinit var shiftService: ShiftService

    @MockitoBean
    private lateinit var jwtService: JwtService

    private val sampleStaffId = UUID.randomUUID()
    private val sampleScId = UUID.randomUUID()

    private val sampleStaffResponse = StaffResponse(
        id = sampleStaffId,
        staffId = "ST-001",
        displayName = "Иванов Иван Иванович",
        serviceCenterId = sampleScId,
        serviceCenterCode = "SC-01",
        profiles = setOf(Profile.INSTALL),
        technologies = setOf(Technology.PON),
        heightPermit = false,
        experienceYears = 2.0,
        shift = ShiftDto(
            start = LocalTime.of(9, 0),
            end = LocalTime.of(18, 0),
            breakDurationMin = 60,
            breakWindow = BreakWindowDto(from = LocalTime.of(12, 0), to = LocalTime.of(15, 0))
        ),
        equipmentStart = EquipmentDto(ont = 1, router = 2, stb = 0),
        equipmentCapacity = EquipmentDto(ont = 4, router = 4, stb = 2)
    )

    @Test
    fun `GET staff returns 200 and list of staff`() {
        `when`(staffService.getAll(null)).thenReturn(listOf(sampleStaffResponse))

        mockMvc.perform(get("/api/v1/staff"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(sampleStaffId.toString()))
            .andExpect(jsonPath("$[0].staff_id").value("ST-001"))
            .andExpect(jsonPath("$[0].display_name").value("Иванов Иван Иванович"))
            .andExpect(jsonPath("$[0].service_center_id").value(sampleScId.toString()))
    }

    @Test
    fun `GET staff by id returns 200 when exists`() {
        `when`(staffService.getById(sampleStaffId)).thenReturn(sampleStaffResponse)

        mockMvc.perform(get("/api/v1/staff/$sampleStaffId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(sampleStaffId.toString()))
            .andExpect(jsonPath("$.staff_id").value("ST-001"))
            .andExpect(jsonPath("$.display_name").value("Иванов Иван Иванович"))
    }

    @Test
    fun `GET staff by id returns 404 when not found`() {
        val unknownId = UUID.randomUUID()
        `when`(staffService.getById(unknownId))
            .thenThrow(NotFoundException("STAFF_NOT_FOUND", "Сотрудник не найден"))

        mockMvc.perform(get("/api/v1/staff/$unknownId"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error.code").value("STAFF_NOT_FOUND"))
            .andExpect(jsonPath("$.error.message").value("Сотрудник не найден"))
    }

    @Test
    fun `POST staff creates new staff and returns 201`() {
        `when`(staffService.create(any())).thenReturn(sampleStaffResponse)

        val jsonBody = """
            {
                "staff_id": "ST-001",
                "display_name": "Иванов Иван Иванович",
                "service_center_id": "$sampleScId",
                "profiles": ["INSTALL"],
                "technologies": ["PON"],
                "height_permit": false,
                "experience_years": 2.0,
                "shift": {
                    "start": "09:00",
                    "end": "18:00",
                    "break_duration_min": 60,
                    "break_window": {
                        "from": "12:00",
                        "to": "15:00"
                    }
                },
                "equipment_start": {
                    "ONT": 1,
                    "ROUTER": 2,
                    "STB": 0
                },
                "equipment_capacity": {
                    "ONT": 4,
                    "ROUTER": 4,
                    "STB": 2
                }
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(sampleStaffId.toString()))
            .andExpect(jsonPath("$.staff_id").value("ST-001"))
            .andExpect(jsonPath("$.display_name").value("Иванов Иван Иванович"))
    }

    @Test
    fun `POST staff with duplicate code returns 409 Conflict`() {
        `when`(staffService.create(any()))
            .thenThrow(ConflictException("STAFF_ALREADY_EXISTS", "Сотрудник уже существует"))

        val jsonBody = """
            {
                "staff_id": "ST-001",
                "display_name": "Иванов Иван Иванович",
                "service_center_id": "$sampleScId",
                "profiles": ["INSTALL"],
                "technologies": ["PON"],
                "shift": {
                    "start": "09:00",
                    "end": "18:00",
                    "break_duration_min": 60,
                    "break_window": { "from": "12:00", "to": "15:00" }
                }
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.error.code").value("STAFF_ALREADY_EXISTS"))
    }

    @Test
    fun `DELETE staff by id returns 204 No Content`() {
        doNothing().`when`(staffService).delete(sampleStaffId)

        mockMvc.perform(delete("/api/v1/staff/$sampleStaffId"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `GET shifts returns 200 and list of shifts`() {
        val shiftId = UUID.randomUUID()
        val shiftResponse = io.github.bbsk.c5d.model.reference.dto.response.ShiftResponse(
            id = shiftId,
            start = LocalTime.of(9, 0),
            end = LocalTime.of(18, 0),
            breakDurationMin = 60,
            breakWindow = BreakWindowDto(from = LocalTime.of(12, 0), to = LocalTime.of(15, 0))
        )

        `when`(shiftService.getAll()).thenReturn(listOf(shiftResponse))

        mockMvc.perform(get("/api/v1/staff/shifts"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(shiftId.toString()))
            .andExpect(jsonPath("$[0].start").value("09:00"))
            .andExpect(jsonPath("$[0].end").value("18:00"))
    }

    @Test
    fun `POST shifts creates and returns 201`() {
        val shiftId = UUID.randomUUID()
        val shiftResponse = io.github.bbsk.c5d.model.reference.dto.response.ShiftResponse(
            id = shiftId,
            start = LocalTime.of(9, 0),
            end = LocalTime.of(18, 0),
            breakDurationMin = 60,
            breakWindow = BreakWindowDto(from = LocalTime.of(12, 0), to = LocalTime.of(15, 0))
        )

        `when`(shiftService.create(any())).thenReturn(shiftResponse)

        val jsonBody = """
            {
                "start": "09:00",
                "end": "18:00",
                "break_duration_min": 60,
                "break_window": {
                    "from": "12:00",
                    "to": "15:00"
                }
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/staff/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(shiftId.toString()))
            .andExpect(jsonPath("$.break_duration_min").value(60))
    }

    @Test
    fun `DELETE shift by id returns 204 No Content`() {
        val shiftId = UUID.randomUUID()
        doNothing().`when`(shiftService).delete(shiftId)

        mockMvc.perform(delete("/api/v1/staff/shifts/$shiftId"))
            .andExpect(status().isNoContent)
    }
}
