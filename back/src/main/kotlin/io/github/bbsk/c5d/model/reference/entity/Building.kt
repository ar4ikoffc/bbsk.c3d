package io.github.bbsk.c5d.model.reference.entity

import io.github.bbsk.c5d.model.reference.enums.BuildingType
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "building")
class Building(
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "code", nullable = false, unique = true, length = 16)
    var code: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "access_node_id", nullable = false)
    var accessNode: AccessNode,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_service_center_id")
    var zoneServiceCenter: ServiceCenter? = null,

    @Column(name = "address", nullable = false, length = 255)
    var address: String,

    @Column(name = "lat", nullable = false)
    var lat: Double,

    @Column(name = "lon", nullable = false)
    var lon: Double,

    @Enumerated(EnumType.STRING)
    @Column(name = "building_type", nullable = false, length = 16)
    var buildingType: BuildingType,

    @Column(name = "overhead_line", nullable = false)
    var overheadLine: Boolean = false
)
