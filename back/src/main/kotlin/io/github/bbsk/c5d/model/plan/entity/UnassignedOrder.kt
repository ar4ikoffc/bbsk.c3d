package io.github.bbsk.c5d.model.plan.entity

import io.github.bbsk.c5d.model.day.entity.WorkDay
import io.github.bbsk.c5d.model.day.entity.WorkOrder
import io.github.bbsk.c5d.model.plan.enums.SuggestedAction
import io.github.bbsk.c5d.model.plan.enums.UnassignedReasonCode
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(
    name = "unassigned_order",
    uniqueConstraints = [UniqueConstraint(name = "uq_unassigned_day_order", columnNames = ["day_id", "order_id"])]
)
class UnassignedOrder(
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_id", nullable = false)
    var day: WorkDay,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    var order: WorkOrder,

    @Enumerated(EnumType.STRING)
    @Column(name = "reason_code", nullable = false, length = 32)
    var reasonCode: UnassignedReasonCode,

    @Column(name = "reason_text", nullable = false, columnDefinition = "TEXT")
    var reasonText: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "suggested_action", nullable = false, length = 32)
    var suggestedAction: SuggestedAction,

    @Column(name = "proposed_slot_start")
    var proposedSlotStart: OffsetDateTime? = null,

    @Column(name = "proposed_slot_end")
    var proposedSlotEnd: OffsetDateTime? = null,

    @Column(name = "since", nullable = false)
    var since: OffsetDateTime
)
