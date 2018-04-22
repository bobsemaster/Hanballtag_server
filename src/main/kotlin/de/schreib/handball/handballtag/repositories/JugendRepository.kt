package de.schreib.handball.handballtag.repositories

import de.schreib.handball.handballtag.entities.Jugend
import org.springframework.data.jpa.repository.JpaRepository

interface JugendRepository: JpaRepository<Jugend, Long>