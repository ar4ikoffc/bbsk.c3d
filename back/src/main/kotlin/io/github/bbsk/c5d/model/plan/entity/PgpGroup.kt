package io.github.bbsk.c5d.model.plan.entity

import io.github.bbsk.c5d.model.day.entity.WorkDay
import io.github.bbsk.c5d.model.day.entity.WorkOrder
import io.github.bbsk.c5d.model.plan.enums.PgpAction
import io.github.bbsk.c5d.model.reference.entity.AccessNode
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "pgp_group")
class PgpGroup(
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "code", nullable = false, unique = true, length = 32)
    var code: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_id", nullable = false)
    var day: WorkDay,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "access_node_id", nullable = false)
    var accessNode: AccessNode,

    @Column(name = "detected_at", nullable = false)
    var detectedAt: OffsetDateTime,

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 32)
    var action: PgpAction = PgpAction.TRANSFER_TO_STATION,

    @Column(name = "comment", columnDefinition = "TEXT")
    var comment: String? = null,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "pgp_group_order",
        joinColumns = [JoinColumn(name = "group_id")],
        inverseJoinColumns = [JoinColumn(name = "order_id")]
    )
    var orders: MutableSet<WorkOrder> = mutableSetOf()
)
