package io.github.bbsk.c5d.controller

import io.github.bbsk.c5d.config.JwtAuthenticationFilter
import io.github.bbsk.c5d.config.JwtService
import io.github.bbsk.c5d.config.SecurityConfig
import io.github.bbsk.c5d.exception.GlobalExceptionHandler
import io.github.bbsk.c5d.model.reference.dto.bulk.LoadResultResponse
import io.github.bbsk.c5d.service.ReferenceService
import io.github.bbsk.c5d.service.TravelMatrixService
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ReferenceController::class)
@Import(GlobalExceptionHandler::class, SecurityConfig::class, JwtAuthenticationFilter::class)
class ReferenceControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var referenceService: ReferenceService

    @MockitoBean
    private lateinit var travelMatrixService: TravelMatrixService

    @MockitoBean
    private lateinit var jwtService: JwtService

    @Test
    fun `PUT reference returns 200 and LoadResult`() {
        val loadResult = LoadResultResponse(
            status = "loaded",
            counts = mapOf(
                "service_centers" to 1,
                "staff" to 1,
                "access_nodes" to 1,
                "buildings" to 1,
                "work_types" to 1
            )
        )
        `when`(referenceService.loadReferenceData(any())).thenReturn(loadResult)

        val json = """
            {
              "service_centers": [
                {
                  "service_center_id": "SC-01",
                  "name": "СЦ Север",
                  "address": "ул. Победы, 30",
                  "lat": 56.89,
                  "lon": 60.61,
                  "opening_hours": { "open": "08:00:00", "close": "21:00:00" },
                  "restock_duration_min": 15
                }
              ],
              "staff": [
                {
                  "staff_id": "TECH-01",
                  "name": "Иванов Иван",
                  "service_center_id": "SC-01",
                  "shift": {
                    "start": "08:00:00",
                    "end": "20:00:00",
                    "break_duration_min": 60,
                    "break_window": {
                      "from": "12:00:00",
                      "to": "15:00:00"
                    }
                  },
                  "profiles": ["INSTALL"],
                  "technologies": ["PON"],
                  "equipment_start": {
                    "ONT": 4,
                    "ROUTER": 2,
                    "STB": 2
                  },
                  "equipment_capacity": {
                    "ONT": 6,
                    "ROUTER": 4,
                    "STB": 4
                  },
                  "height_permit": false,
                  "experience_years": 3.0
                }
              ],
              "access_nodes": [
                {
                  "access_node_id": "AN-0031",
                  "node_type": "OLT",
                  "technology": "PON",
                  "name": "OLT Пионерский-2",
                  "address": "ул. Учителей, 22",
                  "lat": 56.86,
                  "lon": 60.65,
                  "buildings": [
                    {
                      "building_id": "B-00412",
                      "address": "ул. Учителей, 18",
                      "lat": 56.87,
                      "lon": 60.65,
                      "building_type": "MKD",
                      "overhead_line": false,
                      "zone_service_center_id": "SC-01"
                    }
                  ]
                }
              ],
              "work_types": [
                {
                  "code": "INST_PON_MKD",
                  "name": "Инсталляция PON",
                  "duration_min": 90,
                  "deadline_kind": "WORKING_DAYS",
                  "deadline_value": 3,
                  "pgp_relevant": false,
                  "profiles": ["INSTALL"]
                }
              ]
            }
        """.trimIndent()

        mockMvc.perform(
            put("/api/v1/reference")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("loaded"))
            .andExpect(jsonPath("$.counts.service_centers").value(1))
            .andExpect(jsonPath("$.counts.buildings").value(1))
    }

    @Test
    fun `PUT reference travel-matrix returns 200 and points count`() {
        `when`(travelMatrixService.save(any())).thenReturn(2)

        val json = """
            {
              "matrix_id": "ekb-2026-10-v1",
              "points": ["SC-01", "B-00001"],
              "durations_sec": [
                [0, 300],
                [350, 0]
              ],
              "distances_m": [
                [0, 2500],
                [2600, 0]
              ]
            }
        """.trimIndent()

        mockMvc.perform(
            put("/api/v1/reference/travel-matrix")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("loaded"))
            .andExpect(jsonPath("$.counts.points").value(2))
    }
}
