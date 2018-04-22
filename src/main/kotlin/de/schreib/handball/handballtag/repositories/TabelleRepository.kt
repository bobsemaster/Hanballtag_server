package de.schreib.handball.handballtag.repositories

import de.schreib.handball.handballtag.entities.Tabelle
import org.springframework.data.jpa.repository.JpaRepository

interface TabelleRepository : JpaRepository<Tabelle, Long> {
}