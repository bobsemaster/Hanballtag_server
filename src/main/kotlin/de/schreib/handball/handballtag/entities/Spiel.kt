package de.schreib.handball.handballtag.entities

import java.time.Duration
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne


/**
 * Diese Klasse bildet ein Spiel ab. Dabei wird die heim und Gastmannschaft abgespeichert, sowie die
 * Tore die die jeweilige Mannschaft geworfen hat. Ausserdem wird die Halbzeitlänge abgescpeichert und ob es zwei halbzeiten
 * gibt. Falls es nur eine Halbzeit gibt also hasHalfTime = false, dann ist die Halbzeit dauer das gesamte Spiel.
 *
 *  In der variable allGeworfeneTore wird eine Histore der Tore für das Spiel aufgezeichnet, wenn es in echtzeit getrackt wird.
 *
 *  Falls ein Spiel pausiert wurde wird in der Variable isPaused abgespeichert, dass das Spiel pausiert wurde und die Passende
 *  Zeit muss in currentDuration gespeichert werden. Das geschieht mithilfe des aufrufes der copy methode.
 *
 */
@Entity
data class Spiel(
        // Lass hibernate die Id generieren val damit user die id nicht verändern kann
        @Id
        @GeneratedValue
        val id: Long = 0,
        @ManyToOne
        val heimMannschaft: Mannschaft,
        @ManyToOne
        val gastMannschaft: Mannschaft,
        val heimTore: Int = 0,
        val gastTore: Int = 0,
        val hasHalfTime: Boolean = false,
        val halftimeDuration: Duration,
        val dateTime: LocalDateTime,
        val isKampfgerichtAnwesend: Boolean = false,
        val isSchiedsrichterAnwesend: Boolean = false,
        val isHeimmannschaftAnwesend: Boolean = false,
        val isGastMannschaftAnwesend: Boolean = false,
        val spielPlatz: Int? = null,
        @Enumerated(EnumType.STRING)
        val spielTyp: SpielTyp = SpielTyp.NONE,
        @Enumerated(EnumType.STRING)
        val gruppe: Gruppe = Gruppe.A
)

enum class Gruppe {
    A, B, C
}

enum class SpielTyp {
    // Für die Initialisierung des SPiels bei tests
    NONE,
    GRUPPENSPIEL,
    ERSTES_HALBFINALE,
    ZWEITES_HALBFINALE,
    SPIEL_UM_PLATZ_3,
    SPIEL_UM_PLATZ_5,
    ERSTES_SPIEL_UM_PLATZ_5,
    ZWEITES_SPIEL_UM_PLATZ_5,
    DRITTES_SPIEL_UM_PLATZ_5,
    SPIEL_UM_PLATZ_7,
    ERSTES_SPIEL_UM_PLATZ_7,
    ZWEITES_SPIEL_UM_PLATZ_7,
    DRITTES_SPIEL_UM_PLATZ_7,
    SPIEL_UM_PLATZ_9,
    FINALE
}