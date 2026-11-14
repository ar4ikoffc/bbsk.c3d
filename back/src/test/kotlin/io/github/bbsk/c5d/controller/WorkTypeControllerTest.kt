package io.github.bbsk.c5d.controller

import io.github.bbsk.c5d.config.JwtAuthenticationFilter
import io.github.bbsk.c5d.config.JwtService
import io.github.bbsk.c5d.config.SecurityConfig
import io.github.bbsk.c5d.exception.ConflictException
import io.github.bbsk.c5d.exception.GlobalExceptionHandler
import io.github.bbsk.c5d.exception.NotFoundException
import io.github.bbsk.c5d.model.reference.dto.response.WorkTypeResponse
import io.github.bbsk.c5d.model.reference.enums.DeadlineKind
import io.github.bbsk.c5d.model.reference.enums.Profile
import io.github.bbsk.c5d.model.reference.enums.WorkTypeCode
import io.github.bbsk.c5d.service.WorkTypeService
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
import java.util.UUID

@WebMvcTest(WorkTypeController::class)
@Import(GlobalExceptionHandler::class, SecurityConfig::class, JwtAuthenticationFilter::class)
class WorkTypeControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var workTypeService: WorkTypeService

    @MockitoBean
    private lateinit var jwtService: JwtService

    private val sampleId = UUID.randomUUID()
    private val sampleResponse = WorkTypeResponse(
        id = sampleId,
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

    @Test
    fun `GET work-types returns 200 and list`() {
        `when`(workTypeService.getAll()).thenReturn(listOf(sampleResponse))

        mockMvc.perform(get("/api/v1/work-types"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(sampleId.toString()))
            .andExpect(jsonPath("$[0].code").value("INST_PON_MKD"))
            .andExpect(jsonPath("$[0].duration_min").value(90))
            .andExpect(jsonPath("$[0].profiles[0]").value("INSTALL"))
    }

    @Test
    fun `GET work-types by id returns 200 when found`() {
        `when`(workTypeService.getById(sampleId)).thenReturn(sampleResponse)

        mockMvc.perform(get("/api/v1/work-types/$sampleId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(sampleId.toString()))
            .andExpect(jsonPath("$.code").value("INST_PON_MKD"))
            .andExpect(jsonPath("$.name").value("Инсталляция ШПД/IPTV, МКД, PON"))
    }

    @Test
    fun `GET work-types by id returns 404 when not found`() {
        val nonExistentId = UUID.randomUUID()
        `when`(workTypeService.getById(nonExistentId))
            .thenThrow(NotFoundException("WORK_TYPE_NOT_FOUND", "Тип работ не найден"))

        mockMvc.perform(get("/api/v1/work-types/$nonExistentId"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.code").value("WORK_TYPE_NOT_FOUND"))
    }

    @Test
    fun `POST work-types returns 201 on valid request`() {
        `when`(workTypeService.create(any())).thenReturn(sampleResponse)

        val json = """
            {
                "code": "INST_PON_MKD",
                "name": "Инсталляция ШПД/IPTV, МКД, PON",
                "duration_min": 90,
                "max_start_delay_min": 10,
                "deadline_kind": "WORKING_DAYS",
                "deadline_value": 3,
                "pgp_relevant": false,
                "profiles": ["INSTALL"]
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/work-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(sampleId.toString()))
            .andExpect(jsonPath("$.code").value("INST_PON_MKD"))
    }

    @Test
    fun `POST work-types returns 400 on validation error`() {
        val json = """
            {
                "name": "",
                "duration_min": -10,
                "deadline_kind": "WORKING_DAYS",
                "profiles": []
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/work-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
    }

    @Test
    fun `POST work-types returns 409 when code already exists`() {
        `when`(workTypeService.create(any()))
            .thenThrow(ConflictException("WORK_TYPE_ALREADY_EXISTS", "Тип работ с таким кодом уже существует"))

        val json = """
            {
                "code": "INST_PON_MKD",
                "name": "Инсталляция ШПД/IPTV, МКД, PON",
                "duration_min": 90,
                "deadline_kind": "WORKING_DAYS",
                "profiles": ["INSTALL"]
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/work-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.code").value("WORK_TYPE_ALREADY_EXISTS"))
    }

    @Test
    fun `PUT work-types by id returns 200 on valid update`() {
        val updatedResponse = sampleResponse.copy(name = "Обновленное наименование")
        `when`(workTypeService.update(eq(sampleId), any())).thenReturn(updatedResponse)

        val json = """
            {
                "name": "Обновленное наименование"
            }
        """.trimIndent()

        mockMvc.perform(
            put("/api/v1/work-types/$sampleId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Обновленное наименование"))
    }

    @Test
    fun `DELETE work-types by id returns 204 on success`() {
        doNothing().`when`(workTypeService).delete(sampleId)

        mockMvc.perform(delete("/api/v1/work-types/$sampleId"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `DELETE work-types by id returns 404 when not found`() {
        val nonExistentId = UUID.randomUUID()
        doThrow(NotFoundException("WORK_TYPE_NOT_FOUND", "Тип работ не найден"))
            .`when`(workTypeService).delete(nonExistentId)

        mockMvc.perform(delete("/api/v1/work-types/$nonExistentId"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.code").value("WORK_TYPE_NOT_FOUND"))
    }
}
