package io.github.bbsk.c5d.model.day.entity

import io.github.bbsk.c5d.model.reference.entity.Staff
import jakarta.persistence.*
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "work_day")
class WorkDay(
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "date", nullable = false, unique = true)
    var date: LocalDate,

    @Column(name = "current_now", nullable = false)
    var currentNow: OffsetDateTime,

    @Column(name = "plan_version", nullable = false)
    var planVersion: Int = 1,

    @Column(name = "based_on_event_id", length = 64)
    var basedOnEventId: String? = null,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "work_day_absent_staff",
        joinColumns = [JoinColumn(name = "day_id")],
        inverseJoinColumns = [JoinColumn(name = "staff_id")]
    )
    var absentStaff: MutableSet<Staff> = mutableSetOf()
)
