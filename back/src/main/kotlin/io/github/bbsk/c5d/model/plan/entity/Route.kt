package io.github.bbsk.c5d.model.plan.entity

import io.github.bbsk.c5d.model.day.entity.WorkDay
import io.github.bbsk.c5d.model.plan.enums.RouteStatus
import io.github.bbsk.c5d.model.reference.entity.ServiceCenter
import io.github.bbsk.c5d.model.reference.entity.Staff
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(
    name = "route",
    uniqueConstraints = [UniqueConstraint(name = "uq_route_day_staff", columnNames = ["day_id", "staff_id"])]
)
class Route(
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_id", nullable = false)
    var day: WorkDay,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    var staff: Staff,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_center_id", nullable = false)
    var serviceCenter: ServiceCenter,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    var status: RouteStatus = RouteStatus.ACTIVE,

    @Column(name = "unavailable_from")
    var unavailableFrom: OffsetDateTime? = null,

    @Column(name = "unavailable_until")
    var unavailableUntil: OffsetDateTime? = null,

    @OneToMany(mappedBy = "route", cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("seq ASC")
    var stops: MutableList<RouteStop> = mutableListOf()
)
