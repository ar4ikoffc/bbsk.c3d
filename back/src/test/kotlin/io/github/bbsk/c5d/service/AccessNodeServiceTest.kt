package io.github.bbsk.c5d.service

import io.github.bbsk.c5d.exception.ConflictException
import io.github.bbsk.c5d.exception.NotFoundException
import io.github.bbsk.c5d.model.reference.dto.request.CreateAccessNodeRequest
import io.github.bbsk.c5d.model.reference.dto.request.UpdateAccessNodeRequest
import io.github.bbsk.c5d.model.reference.entity.AccessNode
import io.github.bbsk.c5d.model.reference.enums.AccessNodeType
import io.github.bbsk.c5d.model.reference.enums.Technology
import io.github.bbsk.c5d.repository.AccessNodeRepository
import io.github.bbsk.c5d.repository.BuildingRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class AccessNodeServiceTest {

    @Mock
    private lateinit var accessNodeRepository: AccessNodeRepository

    @Mock
    private lateinit var buildingRepository: BuildingRepository

    private lateinit var accessNodeService: AccessNodeService

    @BeforeEach
    fun setUp() {
        accessNodeService = AccessNodeService(accessNodeRepository, buildingRepository)
    }

    @Test
    fun `create access node successfully saves and returns response`() {
        val request = CreateAccessNodeRequest(
            accessNodeId = "AN-0031",
            nodeType = AccessNodeType.OLT,
            technology = Technology.PON,
            name = "OLT Пионерский-2",
            address = "г. Екатеринбург, ул. Учителей, 22",
            lat = 56.8698,
            lon = 60.6510
        )

        `when`(accessNodeRepository.existsByCode("AN-0031")).thenReturn(false)
        `when`(accessNodeRepository.save(any(AccessNode::class.java))).thenAnswer { it.getArgument(0) as AccessNode }

        val response = accessNodeService.create(request)

        assertNotNull(response)
        assertNotNull(response.id)
        assertEquals("AN-0031", response.accessNodeId)
        assertEquals(AccessNodeType.OLT, response.nodeType)
        assertEquals(Technology.PON, response.technology)
        assertEquals("OLT Пионерский-2", response.name)
        verify(accessNodeRepository, times(1)).save(any(AccessNode::class.java))
    }

    @Test
    fun `create access node throws ConflictException when code already exists`() {
        val request = CreateAccessNodeRequest(
            accessNodeId = "AN-0031",
            nodeType = AccessNodeType.OLT,
            technology = Technology.PON,
            name = "OLT",
            address = "Адрес",
            lat = 56.0,
            lon = 60.0
        )

        `when`(accessNodeRepository.existsByCode("AN-0031")).thenReturn(true)

        val ex = assertThrows(ConflictException::class.java) {
            accessNodeService.create(request)
        }
        assertEquals("ACCESS_NODE_ALREADY_EXISTS", ex.code)
    }

    @Test
    fun `getById returns access node when found`() {
        val id = UUID.randomUUID()
        val node = AccessNode(
            id = id,
            code = "AN-0031",
            nodeType = AccessNodeType.SWITCH,
            technology = Technology.FTTB,
            name = "Коммутатор",
            address = "ул. Мира, 19",
            lat = 56.84,
            lon = 60.65
        )

        `when`(accessNodeRepository.findById(id)).thenReturn(Optional.of(node))

        val response = accessNodeService.getById(id)

        assertNotNull(response)
        assertEquals(id, response.id)
        assertEquals("AN-0031", response.accessNodeId)
        assertEquals(Technology.FTTB, response.technology)
    }

    @Test
    fun `getById throws NotFoundException when not found`() {
        val id = UUID.randomUUID()
        `when`(accessNodeRepository.findById(id)).thenReturn(Optional.empty())

        val ex = assertThrows(NotFoundException::class.java) {
            accessNodeService.getById(id)
        }
        assertEquals("ACCESS_NODE_NOT_FOUND", ex.code)
    }

    @Test
    fun `update access node successfully updates fields`() {
        val id = UUID.randomUUID()
        val node = AccessNode(
            id = id,
            code = "AN-0031",
            nodeType = AccessNodeType.OLT,
            technology = Technology.PON,
            name = "Старое имя",
            address = "Старый адрес",
            lat = 56.0,
            lon = 60.0
        )

        `when`(accessNodeRepository.findById(id)).thenReturn(Optional.of(node))
        `when`(accessNodeRepository.save(any(AccessNode::class.java))).thenAnswer { it.getArgument(0) as AccessNode }

        val request = UpdateAccessNodeRequest(
            name = "Новое имя",
            lat = 56.88
        )

        val updated = accessNodeService.update(id, request)

        assertEquals("Новое имя", updated.name)
        assertEquals(56.88, updated.lat)
    }

    @Test
    fun `delete access node throws ConflictException when buildings reference it`() {
        val id = UUID.randomUUID()
        val node = AccessNode(
            id = id,
            code = "AN-0031",
            nodeType = AccessNodeType.OLT,
            technology = Technology.PON,
            name = "OLT",
            address = "Адрес",
            lat = 56.0,
            lon = 60.0
        )

        `when`(accessNodeRepository.findById(id)).thenReturn(Optional.of(node))
        `when`(buildingRepository.existsByAccessNodeId(id)).thenReturn(true)

        val ex = assertThrows(ConflictException::class.java) {
            accessNodeService.delete(id)
        }
        assertEquals("ACCESS_NODE_IN_USE", ex.code)
        verify(accessNodeRepository, never()).delete(any())
    }

    @Test
    fun `delete access node successfully deletes when no buildings reference it`() {
        val id = UUID.randomUUID()
        val node = AccessNode(
            id = id,
            code = "AN-0031",
            nodeType = AccessNodeType.OLT,
            technology = Technology.PON,
            name = "OLT",
            address = "Адрес",
            lat = 56.0,
            lon = 60.0
        )

        `when`(accessNodeRepository.findById(id)).thenReturn(Optional.of(node))
        `when`(buildingRepository.existsByAccessNodeId(id)).thenReturn(false)

        accessNodeService.delete(id)

        verify(accessNodeRepository, times(1)).delete(node)
    }
}
