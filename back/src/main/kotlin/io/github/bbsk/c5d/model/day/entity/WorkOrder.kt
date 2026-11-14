package io.github.bbsk.c5d.model.day.entity

import io.github.bbsk.c5d.model.day.enums.OrderStatus
import io.github.bbsk.c5d.model.reference.entity.AccessNode
import io.github.bbsk.c5d.model.reference.entity.Building
import io.github.bbsk.c5d.model.reference.entity.ServiceCenter
import io.github.bbsk.c5d.model.reference.entity.WorkType
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "work_order")
class WorkOrder(
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "code", nullable = false, unique = true, length = 32)
    var code: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_id", nullable = false)
    var day: WorkDay,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_center_id")
    var serviceCenter: ServiceCenter?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_type_id", nullable = false)
    var workType: WorkType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    var building: Building,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "access_node_id", nullable = false)
    var accessNode: AccessNode,

    @Column(name = "registered_at", nullable = false)
    var registeredAt: OffsetDateTime,

    @Column(name = "slot_start", nullable = false)
    var slotStart: OffsetDateTime,

    @Column(name = "slot_end", nullable = false)
    var slotEnd: OffsetDateTime,

    @Column(name = "latest_start", nullable = false)
    var latestStart: OffsetDateTime,

    @Column(name = "deadline", nullable = false)
    var deadline: OffsetDateTime,

    @Column(name = "required_ont", nullable = false)
    var requiredOnt: Int = 0,

    @Column(name = "required_router", nullable = false)
    var requiredRouter: Int = 0,

    @Column(name = "required_stb", nullable = false)
    var requiredStb: Int = 0,

    @Column(name = "is_repeat", nullable = false)
    var isRepeat: Boolean = false,

    @Column(name = "previous_order_id", length = 32)
    var previousOrderId: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    var status: OrderStatus = OrderStatus.PENDING
)
