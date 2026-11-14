package io.github.bbsk.c5d.repository

import io.github.bbsk.c5d.model.reference.entity.Shift
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalTime
import java.util.UUID

@Repository
interface ShiftRepository : JpaRepository<Shift, UUID> {
    fun findFirstByStartTimeAndEndTimeAndBreakDurationMinAndBreakWindowFromAndBreakWindowTo(
        startTime: LocalTime,
        endTime: LocalTime,
        breakDurationMin: Int,
        breakWindowFrom: LocalTime,
        breakWindowTo: LocalTime
    ): Shift?
}
