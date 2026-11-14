package io.github.bbsk.c5d.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class HealthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `health endpoint returns 200 OK and expected team info`() {
        mockMvc.perform(get("/api/v1/health"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("ok"))
            .andExpect(jsonPath("$.team").value("Бубышки"))
            .andExpect(jsonPath("$.version").value("1.0.0"))
    }
}
