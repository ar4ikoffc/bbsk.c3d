package io.github.bbsk.c5d.repository

import io.github.bbsk.c5d.model.reference.entity.ServiceCenter
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ServiceCenterRepository : JpaRepository<ServiceCenter, UUID> {
    fun findByCode(code: String): ServiceCenter?
    fun existsByCode(code: String): Boolean
}
