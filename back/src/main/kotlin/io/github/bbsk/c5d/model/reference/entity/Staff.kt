package io.github.bbsk.c5d.model.reference.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "staff")
class Staff(
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "code", nullable = false, unique = true, length = 16)
    var code: String,

    @Column(name = "display_name", nullable = false, length = 128)
    var displayName: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_center_id", nullable = false)
    var serviceCenter: ServiceCenter,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    var shift: Shift,

    @Column(name = "height_permit", nullable = false)
    var heightPermit: Boolean = false,

    @Column(name = "experience_years", nullable = false)
    var experienceYears: Double = 0.0,

    @Column(name = "equipment_start_ont", nullable = false)
    var equipmentStartOnt: Int = 0,

    @Column(name = "equipment_start_router", nullable = false)
    var equipmentStartRouter: Int = 0,

    @Column(name = "equipment_start_stb", nullable = false)
    var equipmentStartStb: Int = 0,

    @Column(name = "equipment_capacity_ont", nullable = false)
    var equipmentCapacityOnt: Int = 0,

    @Column(name = "equipment_capacity_router", nullable = false)
    var equipmentCapacityRouter: Int = 0,

    @Column(name = "equipment_capacity_stb", nullable = false)
    var equipmentCapacityStb: Int = 0,

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "staff_profile", joinColumns = [JoinColumn(name = "staff_id")])
    @Column(name = "profile", nullable = false, length = 32)
    var profiles: MutableSet<String> = mutableSetOf(),

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "staff_technology", joinColumns = [JoinColumn(name = "staff_id")])
    @Column(name = "technology", nullable = false, length = 16)
    var technologies: MutableSet<String> = mutableSetOf()
)
