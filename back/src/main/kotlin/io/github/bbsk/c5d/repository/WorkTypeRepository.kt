package io.github.bbsk.c5d.repository

import io.github.bbsk.c5d.model.reference.entity.WorkType
import io.github.bbsk.c5d.model.reference.enums.WorkTypeCode
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface WorkTypeRepository : JpaRepository<WorkType, UUID> {
    @EntityGraph(attributePaths = ["profiles"])
    override fun findById(id: UUID): Optional<WorkType>

    @EntityGraph(attributePaths = ["profiles"])
    fun findByCode(code: WorkTypeCode): WorkType?

    fun existsByCode(code: WorkTypeCode): Boolean

    @EntityGraph(attributePaths = ["profiles"])
    override fun findAll(): List<WorkType>
}
