package io.github.bbsk.c5d.model.reference.entity

import io.github.bbsk.c5d.model.reference.enums.DeadlineKind
import io.github.bbsk.c5d.model.reference.enums.Profile
import io.github.bbsk.c5d.model.reference.enums.WorkTypeCode
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "work_type")
class WorkType(
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false, unique = true, length = 32)
    var code: WorkTypeCode,

    @Column(name = "name", nullable = false, length = 255)
    var name: String,

    @Column(name = "duration_min", nullable = false)
    var durationMin: Int,

    @Column(name = "max_start_delay_min")
    var maxStartDelayMin: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "deadline_kind", nullable = false, length = 16)
    var deadlineKind: DeadlineKind,

    @Column(name = "deadline_value")
    var deadlineValue: Int? = null,

    @Column(name = "repeat_deadline_value")
    var repeatDeadlineValue: Int? = null,

    @Column(name = "pgp_relevant", nullable = false)
    var pgpRelevant: Boolean = false,

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "work_type_profile", joinColumns = [JoinColumn(name = "work_type_id")])
    @Enumerated(EnumType.STRING)
    @Column(name = "profile", nullable = false, length = 32)
    var profiles: MutableSet<Profile> = mutableSetOf()
)
