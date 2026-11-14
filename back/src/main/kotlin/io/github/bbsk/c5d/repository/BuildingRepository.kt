package io.github.bbsk.c5d.repository

import io.github.bbsk.c5d.model.reference.entity.Building
import io.github.bbsk.c5d.model.reference.enums.BuildingType
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface BuildingRepository : JpaRepository<Building, UUID> {
    @EntityGraph(attributePaths = ["accessNode", "zoneServiceCenter"])
    override fun findById(id: UUID): Optional<Building>

    @EntityGraph(attributePaths = ["accessNode", "zoneServiceCenter"])
    fun findByCode(code: String): Building?

    fun existsByCode(code: String): Boolean

    @EntityGraph(attributePaths = ["accessNode", "zoneServiceCenter"])
    override fun findAll(): List<Building>

    @EntityGraph(attributePaths = ["accessNode", "zoneServiceCenter"])
    fun findAllByBuildingType(buildingType: BuildingType): List<Building>

    @EntityGraph(attributePaths = ["accessNode", "zoneServiceCenter"])
    fun findAllByAccessNodeId(accessNodeId: UUID): List<Building>

    @EntityGraph(attributePaths = ["accessNode", "zoneServiceCenter"])
    fun findAllByZoneServiceCenterId(zoneServiceCenterId: UUID): List<Building>

    fun existsByAccessNodeId(accessNodeId: UUID): Boolean

    fun existsByZoneServiceCenterId(zoneServiceCenterId: UUID): Boolean
}
