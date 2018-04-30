package de.schreib.handball.handballtag.entities

import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.persistence.*


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
        val hasHalfTime: Boolean = true,
        val halftimeDuration: Duration = Duration.of(15, ChronoUnit.MINUTES),
        val currentDuration: Duration = Duration.ZERO,
        val isPaused: Boolean = false,
        val dateTime: LocalDateTime,
        val isKampfgerichtAnwesend: Boolean = false,
        val isSchiedsrichterAnwesend:Boolean = false,
        val isHeimmannschaftAnwesend: Boolean = false,
        val isGastMannschaftAnwesend:Boolean = false,
        val spielPlatz:String = "Noch nicht festgelegt",
        val spielTyp:SpielTyp = SpielTyp.NONE,

        @OneToMany(
                mappedBy = "mannschaft",
                cascade = [CascadeType.ALL],
                orphanRemoval = true,
                fetch = FetchType.EAGER

        )
        val allGeworfeneTore: List<SpielTor> = emptyList()
)

enum class SpielTyp {
        // Für die Initialisierung des SPiels bei tests
        NONE,
        GRUPPENSPIEL,
        KO_PHASE
}

/**
 * Ein SpielTor spiegelt ein Tor das In einem Spiel gefallen ist wieder, dazu wird die Mannschaft, die das Spiel geworfen Hat
 * gespeichert und nach wie vielen Minuten das Tor gefallen ist.
 */
@Entity
data class SpielTor(
        // Lass hibernate die Id generieren val damit user die id nicht verändern kann
        @Id
        @GeneratedValue
        val id: Long = 0,
        @ManyToOne
        @JoinColumn(name = "spiel_id")
        val mannschaft: Mannschaft,
        val time: Duration
)