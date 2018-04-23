package de.schreib.handball.handballtag.entities

import java.time.Duration
import java.time.LocalDateTime
import javax.persistence.*


/**
 * Diese Klasse bildet ein Spiel ab. Dabei wird die heim und Gastmannschaft abgespeichert, sowie die
 * Tore die die jeweilige Mannschaft geworfen hat. Ausserdem wird die Halbzeitlänge abgescpeichert und ob es zwei halbzeiten
 * gibt. Falls es nur eine Halbzeit gibt also hasHalfTime = false, dann ist die Halbzeit dauer das gesamte Spiel.
 *
 *  In der variable allGeworfeneTore wird eine Histore der Tore für das Spiel aufgezeichnet, wenn es in echtzeit getrackt wird.
 *
 */
@Entity
data class Spiel(
        @Id
        @GeneratedValue
        val id: Long,
        @ManyToOne
        val heimMannschaft: Mannschaft,
        @ManyToOne
        val gastMannschaft: Mannschaft,
        val heimTore: Int,
        val gastTore: Int,
        val hasHalfTime: Boolean,
        val halftimeDuration: Duration,
        val currentDuration: Duration,
        val dateTime:LocalDateTime,

        @OneToMany(
                mappedBy = "mannschaft",
                cascade = [CascadeType.ALL],
                orphanRemoval = true,
                fetch = FetchType.EAGER

        )
        val allGeworfeneTore: List<SpielTor>
)

/**
 * Ein SpielTor spiegelt ein Tor das In einem Spiel gefallen ist wieder, dazu wird die Mannschaft, die das Spiel geworfen Hat
 * gespeichert und nach wie vielen Minuten das Tor gefallen ist.
 */
@Entity
data class SpielTor(
        @Id
        @GeneratedValue
        val id: Long,
        @ManyToOne
        @JoinColumn(name = "spiel_id")
        val mannschaft: Mannschaft,
        val time: Duration
)