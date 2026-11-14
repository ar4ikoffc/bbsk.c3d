package io.github.bbsk.c5d.repository

import io.github.bbsk.c5d.model.reference.entity.AccessNode
import io.github.bbsk.c5d.model.reference.enums.AccessNodeType
import io.github.bbsk.c5d.model.reference.enums.Technology
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AccessNodeRepository : JpaRepository<AccessNode, UUID> {
    fun findByCode(code: String): AccessNode?

    fun existsByCode(code: String): Boolean

    fun findAllByTechnology(technology: Technology): List<AccessNode>

    fun findAllByNodeType(nodeType: AccessNodeType): List<AccessNode>
}
