package de.schreib.handball.handballtag.repositories

import de.schreib.handball.handballtag.entities.Mannschaft
import de.schreib.handball.handballtag.entities.Verein
import org.springframework.data.jpa.repository.JpaRepository

interface MannschaftRepository : JpaRepository<Mannschaft, Long> {
    fun findAllByVerein(verein: Verein): List<Mannschaft>
}