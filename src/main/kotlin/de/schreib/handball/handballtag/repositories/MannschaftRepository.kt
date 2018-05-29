package de.schreib.handball.handballtag.repositories

import de.schreib.handball.handballtag.entities.Jugend
import de.schreib.handball.handballtag.entities.Mannschaft
import de.schreib.handball.handballtag.entities.Verein
import org.springframework.data.jpa.repository.JpaRepository
import kotlin.reflect.jvm.internal.impl.storage.MemoizedFunctionToNotNull

interface MannschaftRepository : JpaRepository<Mannschaft, Long> {
    fun findAllByVerein(verein: Verein): List<Mannschaft>
    fun findAllByJugend(jugend: Jugend):List<Mannschaft>
    fun deleteAllByVerein(verein: Verein)
}