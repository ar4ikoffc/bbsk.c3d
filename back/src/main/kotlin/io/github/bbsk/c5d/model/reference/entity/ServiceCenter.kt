package io.github.bbsk.c5d.model.reference.entity

import jakarta.persistence.*
import java.time.LocalTime
import java.util.UUID

@Entity
@Table(name = "service_center")
class ServiceCenter(
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "code", nullable = false, unique = true, length = 16)
    var code: String,

    @Column(name = "name", nullable = false, length = 128)
    var name: String,

    @Column(name = "address", nullable = false, length = 255)
    var address: String,

    @Column(name = "lat", nullable = false)
    var lat: Double,

    @Column(name = "lon", nullable = false)
    var lon: Double,

    @Column(name = "open_time", nullable = false)
    var openTime: LocalTime,

    @Column(name = "close_time", nullable = false)
    var closeTime: LocalTime,

    @Column(name = "restock_duration_min", nullable = false)
    var restockDurationMin: Int = 15
)
