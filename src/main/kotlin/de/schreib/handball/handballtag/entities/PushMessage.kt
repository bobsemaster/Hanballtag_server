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
    var targetTopic: TargetTopic
)

enum class TargetTopic {
    DEFAULT,
    KAMPFGERICHT,
    TURNIERLEITUNG
}

