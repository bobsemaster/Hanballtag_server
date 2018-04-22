package de.schreib.handball.handballtag.repositories

import de.schreib.handball.handballtag.entities.Mannschaft
import org.springframework.data.jpa.repository.JpaRepository

interface MannschaftRepository : JpaRepository<Mannschaft, Long>