package io.github.bbsk.c5d.model.event.entity

import io.github.bbsk.c5d.model.day.entity.WorkDay
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "event")
class Event(
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_id", nullable = false)
    var day: WorkDay,

    @Column(name = "code", nullable = false, length = 64)
    var code: String,

    @Column(name = "type", nullable = false, length = 32)
    var type: String,

    @Column(name = "timestamp", nullable = false)
    var timestamp: OffsetDateTime,

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    var payload: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now()
)
