package de.schreib.handball.handballtag.repositories

import de.schreib.handball.handballtag.entities.Jugend
import de.schreib.handball.handballtag.entities.Tabelle
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TabelleRepository : JpaRepository<Tabelle, Long> {
    fun findByJugend(jugend: Jugend): Optional<Tabelle>
}