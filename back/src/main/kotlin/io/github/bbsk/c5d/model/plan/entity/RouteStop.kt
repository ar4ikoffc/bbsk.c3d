package io.github.bbsk.c5d.model.plan.entity

import io.github.bbsk.c5d.model.day.entity.WorkOrder
import io.github.bbsk.c5d.model.plan.enums.StopStatus
import io.github.bbsk.c5d.model.plan.enums.StopType
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "route_stop")
class RouteStop(
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    var route: Route,

    @Column(name = "seq", nullable = false)
    var seq: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 16)
    var type: StopType,

    @Column(name = "location_code", nullable = false, length = 32)
    var locationCode: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    var order: WorkOrder? = null,

    @Column(name = "arrival", nullable = false)
    var arrival: OffsetDateTime,

    @Column(name = "start_time", nullable = false)
    var startTime: OffsetDateTime,

    @Column(name = "end_time", nullable = false)
    var endTime: OffsetDateTime,

    @Column(name = "travel_sec", nullable = false)
    var travelSec: Int = 0,

    @Column(name = "distance_m", nullable = false)
    var distanceM: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    var status: StopStatus = StopStatus.PLANNED,

    @Column(name = "equipment_after_ont")
    var equipmentAfterOnt: Int? = null,

    @Column(name = "equipment_after_router")
    var equipmentAfterRouter: Int? = null,

    @Column(name = "equipment_after_stb")
    var equipmentAfterStb: Int? = null
)
