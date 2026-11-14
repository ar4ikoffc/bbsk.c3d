package io.github.bbsk.c5d.model.reference.entity

import io.github.bbsk.c5d.model.reference.enums.AccessNodeType
import io.github.bbsk.c5d.model.reference.enums.Technology
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

    @Enumerated(EnumType.STRING)
    @Column(name = "node_type", nullable = false, length = 16)
    var nodeType: AccessNodeType,

    @Enumerated(EnumType.STRING)
    @Column(name = "technology", nullable = false, length = 16)
    var technology: Technology,

    @Column(name = "name", nullable = false, length = 128)
    var name: String,

    @Column(name = "address", nullable = false, length = 255)
    var address: String,

    @Column(name = "lat", nullable = false)
    var lat: Double,

    @Column(name = "lon", nullable = false)
    var lon: Double
)
