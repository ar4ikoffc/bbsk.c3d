package io.github.bbsk.c5d.repository

import io.github.bbsk.c5d.model.user.entity.AppUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AppUserRepository : JpaRepository<AppUser, UUID> {
    fun findByUsername(username: String): AppUser?
    fun existsByUsername(username: String): Boolean
}
