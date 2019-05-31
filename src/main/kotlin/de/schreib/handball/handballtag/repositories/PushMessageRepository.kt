package de.schreib.handball.handballtag.repositories

import de.schreib.handball.handballtag.entities.PushMessage
import org.springframework.data.jpa.repository.JpaRepository

interface PushMessageRepository : JpaRepository<PushMessage, Long> {
}

