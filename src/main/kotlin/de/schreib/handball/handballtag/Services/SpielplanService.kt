package de.schreib.handball.handballtag.Services

import de.schreib.handball.handballtag.entities.Jugend
import de.schreib.handball.handballtag.entities.Spiel
import de.schreib.handball.handballtag.repositories.SpielRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

@Service

class SpielplanService(@Autowired val spielRepository: SpielRepository) {

    fun addPauseToJugenden(allJugend: List<Jugend>, pauseStartTime: LocalDateTime, pauseDuration: Duration) {
        val allJugendSpiele = spielRepository.findAllByAllJugend(allJugend)
        val allUpdateSpiele = allJugendSpiele.filter { it.dateTime > pauseStartTime && it.dateTime.dayOfMonth == pauseStartTime.dayOfMonth }
        spielRepository.saveAll(allUpdateSpiele.map { it.copy(dateTime = it.dateTime.plus(pauseDuration)) })
    }

    fun changePlatzOfSpiel(spiel: Spiel, newPlatz: Int, pauseDuration: Duration) {
        val allSpielOnNewPlatz = spielRepository.findAllBySpielPlatz(newPlatz).sortedBy { it.dateTime }
        val lastSpielOnPlatz = allSpielOnNewPlatz.last()
        val newDateTime = lastSpielOnPlatz.dateTime.plus(lastSpielOnPlatz.halftimeDuration).plus(pauseDuration)
        spielRepository.save(spiel.copy(spielPlatz = newPlatz, dateTime = newDateTime))
    }
}