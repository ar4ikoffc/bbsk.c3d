package io.github.bbsk.c5d.model.reference.entity

import jakarta.persistence.*
import java.time.LocalTime
import java.util.UUID

@Entity
@Table(name = "shift")
class Shift(
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "start_time", nullable = false)
    var startTime: LocalTime,

    @Column(name = "end_time", nullable = false)
    var endTime: LocalTime,

    @Column(name = "break_duration_min", nullable = false)
    var breakDurationMin: Int = 60,

    @Column(name = "break_window_from", nullable = false)
    var breakWindowFrom: LocalTime,

    @Column(name = "break_window_to", nullable = false)
    var breakWindowTo: LocalTime
)
