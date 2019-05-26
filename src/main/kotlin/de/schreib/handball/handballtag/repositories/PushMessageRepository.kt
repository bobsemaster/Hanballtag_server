package de.schreib.handball.handballtag.repositories

import de.schreib.handball.handballtag.entities.PushMessage
import de.schreib.handball.handballtag.entities.Severity
import org.springframework.data.jpa.repository.JpaRepository

interface PushMessageRepository : JpaRepository<PushMessage, Long> {
    fun findAllBySeverity(severity: Severity): List<PushMessage>
}

