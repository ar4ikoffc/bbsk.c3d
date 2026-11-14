package io.github.bbsk.c5d.model.reference.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "access_node")
class AccessNode(
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "code", nullable = false, unique = true, length = 16)
    var code: String,

    @Column(name = "node_type", nullable = false, length = 16)
    var nodeType: String,

    @Column(name = "technology", nullable = false, length = 16)
    var technology: String,

    @Column(name = "name", nullable = false, length = 128)
    var name: String,

    @Column(name = "address", nullable = false, length = 255)
    var address: String,

    @Column(name = "lat", nullable = false)
    var lat: Double,

    @Column(name = "lon", nullable = false)
    var lon: Double
)
