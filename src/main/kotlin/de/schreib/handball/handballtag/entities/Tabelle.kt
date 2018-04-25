package de.schreib.handball.handballtag.entities

import javax.persistence.*

/**
 * In einer Tabelle wird die dazugehörige Jugend und die Mannschaften gespeichert, die in dieser Jugend Spielen.
 * Die punkteverhätnisse sind in den Mannschaften gespeichert und es wird daraus eine Tabelle generiert wenn diese
 * Angefragt wird
 */
@Entity
data class Tabelle(
        // Lass hibernate die Id generieren val damit user die id nicht verändern kann
        @Id
        @GeneratedValue
        val id: Long = 0,
        @OneToMany(
                mappedBy = "verein",
                cascade = [CascadeType.ALL],
                orphanRemoval = true

        )
        val allMannschaft: List<Mannschaft>,
        val jugend: Jugend
)
