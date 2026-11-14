package io.github.bbsk.c5d.model.plan.entity

import io.github.bbsk.c5d.model.day.entity.WorkDay
import io.github.bbsk.c5d.model.day.entity.WorkOrder
import io.github.bbsk.c5d.model.plan.enums.AlertStatus
import io.github.bbsk.c5d.model.plan.enums.AlertSuggestedAction
import io.github.bbsk.c5d.model.plan.enums.AlertType
import io.github.bbsk.c5d.model.reference.entity.Staff
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "alert")
class Alert(
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "code", nullable = false, unique = true, length = 32)
    var code: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_id", nullable = false)
    var day: WorkDay,

    @Column(name = "created_at", nullable = false)
    var createdAt: OffsetDateTime,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 32)
    var type: AlertType,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    var status: AlertStatus = AlertStatus.ACTIVE,

    @Column(name = "resolved_at")
    var resolvedAt: OffsetDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    var order: WorkOrder? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    var staff: Staff? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "suggested_action", length = 32)
    var suggestedAction: AlertSuggestedAction? = null,

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    var message: String,

    @Column(name = "applied", nullable = false)
    var applied: Boolean = false
)
