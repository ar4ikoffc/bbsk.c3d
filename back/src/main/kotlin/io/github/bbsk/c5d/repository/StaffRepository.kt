package io.github.bbsk.c5d.repository

import io.github.bbsk.c5d.model.reference.entity.Staff
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface StaffRepository : JpaRepository<Staff, UUID> {
    @EntityGraph(attributePaths = ["serviceCenter", "shift", "profiles", "technologies"])
    override fun findById(id: UUID): Optional<Staff>

    @EntityGraph(attributePaths = ["serviceCenter", "shift", "profiles", "technologies"])
    fun findByCode(code: String): Staff?

    fun existsByCode(code: String): Boolean

    fun existsByShiftId(shiftId: UUID): Boolean

    fun existsByServiceCenterId(serviceCenterId: UUID): Boolean

    @EntityGraph(attributePaths = ["serviceCenter", "shift", "profiles", "technologies"])
    fun findAllByServiceCenterId(serviceCenterId: UUID): List<Staff>

    @EntityGraph(attributePaths = ["serviceCenter", "shift", "profiles", "technologies"])
    override fun findAll(): List<Staff>
}
