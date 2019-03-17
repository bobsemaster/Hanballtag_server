package de.schreib.handball.handballtag.services

import de.schreib.handball.handballtag.entities.Jugend
import de.schreib.handball.handballtag.entities.Spiel
import de.schreib.handball.handballtag.repositories.SpielRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

@Service

class SpielplanService(
    @Autowired
    val spielRepository: SpielRepository
) {
    val log = LoggerFactory.getLogger(this.javaClass)


    fun addPauseToJugenden(allJugend: List<Jugend>, pauseStartTime: LocalDateTime, pauseDuration: Duration) {
        val allJugendSpiele = spielRepository.findAllByAllJugend(allJugend)
        val allUpdateSpiele =
            allJugendSpiele.filter { it.dateTime >= pauseStartTime && it.dateTime.dayOfMonth == pauseStartTime.dayOfMonth }
        if (allUpdateSpiele.isEmpty()) {
            log.error("Es gibt keine spiele die Verschoben werden müssen")
        }
        spielRepository.saveAll(allUpdateSpiele.map { it.copy(dateTime = it.dateTime.plus(pauseDuration)) })
    }

    fun changePlatzOfSpiel(spiel: Spiel, newPlatz: Int, pauseDuration: Duration) {
        val allSpielOnNewPlatz =
            spielRepository.findAllBySpielPlatz(newPlatz).filter { it.dateTime.dayOfMonth == spiel.dateTime.dayOfMonth }
                .sortedBy { it.dateTime }
        val lastSpielOnPlatz = allSpielOnNewPlatz.last()
        val newDateTime = lastSpielOnPlatz.dateTime.plus(lastSpielOnPlatz.halftimeDuration).plus(pauseDuration)
        val allSpielOnOldPlatz = spielRepository.findAllBySpielPlatz(spiel.spielPlatz!!)
            .filter { it.dateTime >= spiel.dateTime && it.dateTime.dayOfMonth == spiel.dateTime.dayOfMonth }
        // Nachfolgende Spiele vorverlegen
        spielRepository.saveAll(allSpielOnOldPlatz.map {
            it.copy(
                dateTime = it.dateTime.minus(spiel.halftimeDuration).minus(
                    pauseDuration
                )
            )
        })
        spielRepository.save(spiel.copy(spielPlatz = newPlatz, dateTime = newDateTime))
    }
}
