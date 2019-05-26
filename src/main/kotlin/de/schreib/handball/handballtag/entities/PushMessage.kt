package de.schreib.handball.handballtag.entities

import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class PushMessage(
    @GeneratedValue
    @Id
    var id: Long?, var title: String, var content: String,
    @Enumerated(EnumType.STRING)
    var severity: Severity,
    @Enumerated(EnumType.STRING)
    var targetAudience: TargetAudience
)

enum class Severity {
    INFO,
    IMPORTANT,
}

enum class TargetAudience {
    ALL,
    KAMPFGERICHT,
    TURNIERLEITUNG
}

