package io.github.bbsk.c5d.service

import io.github.bbsk.c5d.exception.ConflictException
import io.github.bbsk.c5d.exception.NotFoundException
import io.github.bbsk.c5d.model.reference.dto.request.CreateAccessNodeRequest
import io.github.bbsk.c5d.model.reference.dto.request.UpdateAccessNodeRequest
import io.github.bbsk.c5d.model.reference.dto.response.AccessNodeResponse
import io.github.bbsk.c5d.model.reference.entity.AccessNode
import io.github.bbsk.c5d.model.reference.enums.Technology
import io.github.bbsk.c5d.repository.AccessNodeRepository
import io.github.bbsk.c5d.repository.BuildingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AccessNodeService(
    private val accessNodeRepository: AccessNodeRepository,
    private val buildingRepository: BuildingRepository
) {

    @Transactional(readOnly = true)
    fun getAll(technology: Technology? = null): List<AccessNodeResponse> {
        val list = if (technology != null) {
            accessNodeRepository.findAllByTechnology(technology)
        } else {
            accessNodeRepository.findAll()
        }
        return list.map { it.toDto() }
    }

    @Transactional(readOnly = true)
    fun getById(id: UUID): AccessNodeResponse {
        val node = accessNodeRepository.findById(id).orElseThrow {
            NotFoundException("ACCESS_NODE_NOT_FOUND", "Узел доступа с id '$id' не найден")
        }
        return node.toDto()
    }

    @Transactional(readOnly = true)
    fun getByCode(code: String): AccessNodeResponse {
        val node = accessNodeRepository.findByCode(code.trim())
            ?: throw NotFoundException("ACCESS_NODE_NOT_FOUND", "Узел доступа с кодом '$code' не найден")
        return node.toDto()
    }

    @Transactional
    fun create(request: CreateAccessNodeRequest): AccessNodeResponse {
        val trimmedCode = request.accessNodeId.trim()
        if (accessNodeRepository.existsByCode(trimmedCode)) {
            throw ConflictException(
                "ACCESS_NODE_ALREADY_EXISTS",
                "Узел доступа с кодом '$trimmedCode' уже существует"
            )
        }

        val node = AccessNode(
            id = UUID.randomUUID(),
            code = trimmedCode,
            nodeType = request.nodeType,
            technology = request.technology,
            name = request.name.trim(),
            address = request.address.trim(),
            lat = request.lat,
            lon = request.lon
        )

        val saved = accessNodeRepository.save(node)
        return saved.toDto()
    }

    @Transactional
    fun update(id: UUID, request: UpdateAccessNodeRequest): AccessNodeResponse {
        val node = accessNodeRepository.findById(id).orElseThrow {
            NotFoundException("ACCESS_NODE_NOT_FOUND", "Узел доступа с id '$id' не найден")
        }

        if (request.accessNodeId != null) {
            val trimmedCode = request.accessNodeId.trim()
            if (trimmedCode != node.code && accessNodeRepository.existsByCode(trimmedCode)) {
                throw ConflictException(
                    "ACCESS_NODE_ALREADY_EXISTS",
                    "Узел доступа с кодом '$trimmedCode' уже существует"
                )
            }
            node.code = trimmedCode
        }

        if (request.nodeType != null) {
            node.nodeType = request.nodeType
        }
        if (request.technology != null) {
            node.technology = request.technology
        }
        if (request.name != null) {
            node.name = request.name.trim()
        }
        if (request.address != null) {
            node.address = request.address.trim()
        }
        if (request.lat != null) {
            node.lat = request.lat
        }
        if (request.lon != null) {
            node.lon = request.lon
        }

        val updated = accessNodeRepository.save(node)
        return updated.toDto()
    }

    @Transactional
    fun delete(id: UUID) {
        val node = accessNodeRepository.findById(id).orElseThrow {
            NotFoundException("ACCESS_NODE_NOT_FOUND", "Узел доступа с id '$id' не найден")
        }

        if (buildingRepository.existsByAccessNodeId(id)) {
            throw ConflictException(
                "ACCESS_NODE_IN_USE",
                "Невозможно удалить узел доступа '${node.code}', так как к нему привязаны здания"
            )
        }

        accessNodeRepository.delete(node)
    }

    private fun AccessNode.toDto(): AccessNodeResponse {
        return AccessNodeResponse(
            id = id,
            accessNodeId = code,
            nodeType = nodeType,
            technology = technology,
            name = name,
            address = address,
            lat = lat,
            lon = lon
        )
    }
}
