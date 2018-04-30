package de.schreib.handball.handballtag.repositories

import de.schreib.handball.handballtag.entities.Verkauf
import de.schreib.handball.handballtag.entities.VerkaufArtikel
import org.springframework.data.jpa.repository.JpaRepository

interface VerkaufRepository : JpaRepository<Verkauf, Long>

interface VerkaufArtikelRepository : JpaRepository<VerkaufArtikel, Long>