package de.schreib.handball.handballtag.repositories

import de.schreib.handball.handballtag.entities.Verein
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface VereinRepository : JpaRepository<Verein, Long> {
    fun findByName(name: String): Verein?
}